package gview.view.commitinfo

import gview.model.GvRepository
import gview.view.framework.GvBaseWindowCtrl
import gview.view.menu.WorkTreeMenu
import gview.view.util.GvColumnAdjuster
import gview.model.commit.GvCommitFile
import javafx.beans.property.SimpleIntegerProperty
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.AnchorPane

class WorkFileListCtrl: GvBaseWindowCtrl() {

    @FXML private lateinit var workFileListPane: SplitPane

    @FXML private lateinit var stagedFileTopBox: AnchorPane
    @FXML private lateinit var stagedFileList: TableView<RowData>
    @FXML private lateinit var stagedFileBottomBox: AnchorPane
    @FXML private lateinit var stagedFileTypeColumn: TableColumn<RowData, String>
    @FXML private lateinit var stagedFilePathColumn: TableColumn<RowData, String>

    @FXML private lateinit var changedFileTopBox: AnchorPane
    @FXML private lateinit var changedFileList: TableView<RowData>
    @FXML private lateinit var changedFileBottomBox: AnchorPane
    @FXML private lateinit var changedFileTypeColumn: TableColumn<RowData, String>
    @FXML private lateinit var changedFilePathColumn: TableColumn<RowData, String>

    @FXML private lateinit var stageButton: Button
    @FXML private lateinit var unStageButton: Button
    @FXML private lateinit var commitButton: Button

    /* テーブルデータ */
    class RowData(val diffEntry: GvCommitFile) {
        val type: String = diffEntry.typeName
        val path: String = diffEntry.path
    }

    private val stagedFileNumber  = SimpleIntegerProperty(0)
    private val changedFileNumber = SimpleIntegerProperty(0)

    /* テーブルのカラム幅を調整する処理クラス */
    private lateinit var stagedFileListAdjuster: GvColumnAdjuster
    private lateinit var changedFileListAdjuster: GvColumnAdjuster

    //初期化
    fun initialize() {
        stagedFileTypeColumn.cellValueFactory = PropertyValueFactory("type")
        stagedFilePathColumn.cellValueFactory = PropertyValueFactory("path")

        changedFileTypeColumn.cellValueFactory = PropertyValueFactory("type")
        changedFilePathColumn.cellValueFactory = PropertyValueFactory("path")

        stagedFileTopBox.style = CSS.topBoxStyle
        stagedFileBottomBox.style = CSS.bottomBoxStyle
        stagedFileTypeColumn.style = CSS.typeColumnStyle
        stagedFilePathColumn.style = CSS.pathColumnStyle
        stagedFileList.style = CSS.fileListStyle
        stagedFileList.placeholder = Label()

        changedFileTopBox.style = CSS.topBoxStyle
        changedFileBottomBox.style = CSS.bottomBoxStyle
        changedFileTypeColumn.style = CSS.typeColumnStyle
        changedFilePathColumn.style = CSS.pathColumnStyle
        changedFileList.style = CSS.fileListStyle
        changedFileList.placeholder = Label()

        stagedFileListAdjuster = GvColumnAdjuster(stagedFileList, stagedFilePathColumn)
        changedFileListAdjuster = GvColumnAdjuster(changedFileList, changedFilePathColumn)

        stageButton.disableProperty().bind(changedFileNumber.isEqualTo(0))
        unStageButton.disableProperty().bind(stagedFileNumber.isEqualTo(0))
        commitButton.disableProperty().bind(stagedFileNumber.isEqualTo(0))

        commitButton.setOnAction { WorkTreeMenu.doCommitCommand() }
        unStageButton.setOnAction { WorkTreeMenu.doUnStageCommand() }
        stageButton.setOnAction { WorkTreeMenu.doStageCommand() }
    }

    //表示完了時にListenerを設定する
    override fun displayCompleted() {
        GvRepository.currentRepository?.workFiles?.stagedFiles?.addListener  { _, _, new -> updateStagedFiles(new)  }
        GvRepository.currentRepository?.workFiles?.changedFiles?.addListener { _, _, new -> updateChangedFiles(new) }

        stagedFileList.selectionModel.selectedItemProperty().addListener { _, _, entry ->
            CommitDiff.controller.selectDiffEntry(entry?.diffEntry)
        }
        changedFileList.selectionModel.selectedItemProperty().addListener { _, _, entry ->
            CommitDiff.controller.selectDiffEntry(entry?.diffEntry)
        }

        stagedFileListAdjuster.adjustColumnWidth()
        changedFileListAdjuster.adjustColumnWidth()
    }

    private fun updateStagedFiles(files: List<GvCommitFile>?) {
        if(files != null) {
            stagedFileList.items.setAll(files.map { RowData(it) })
        } else {
            stagedFileList.items.clear()
        }
        stagedFileNumber.value = stagedFileList.items.size
    }

    private fun updateChangedFiles(files: List<GvCommitFile>?) {
        if(files != null) {
            changedFileList.items.setAll(files.map { RowData(it) })
        } else {
            changedFileList.items.clear()
        }
        changedFileNumber.value = changedFileList.items.size
    }

    object CSS {
        val topBoxStyle = """
            -fx-padding: 0 10 0 10;
            -fx-background-color: -background-color;
        """.trimIndent()

        val fileListStyle = """
            -fx-padding: 5 8 5 8;
            -fx-background-color: -background-color;
        """.trimIndent()

        val bottomBoxStyle = """
            -fx-padding: 0 10 10 10;
            -fx-background-color: -background-color;
        """.trimIndent()

        val typeColumnStyle = """
            -fx-alignment: CENTER;
        """.trimIndent()

        val pathColumnStyle = """
        """.trimIndent()
    }
}
