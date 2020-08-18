package gview.gui.commitinfo

import gview.gui.framework.GviewBasePane
import gview.gui.framework.GviewBasePaneCtrl
import gview.gui.main.WorkTreeMenu
import gview.gui.util.TableColumnAdjuster
import gview.model.commit.GviewGitFileEntryModel
import javafx.beans.property.SimpleIntegerProperty
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.AnchorPane

object HeaderFileList: GviewBasePane<HeaderFileListCtrl>(
        "/view/HeaderFileListView.fxml",
        "HeaderFileList")

class HeaderFileListCtrl: GviewBasePaneCtrl() {

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
    class RowData(val diffEntry: GviewGitFileEntryModel) {
        val type: String = diffEntry.typeName
        val path: String = diffEntry.path
    }

    private val stagedFileNumber  = SimpleIntegerProperty(0)
    private val changedFileNumber = SimpleIntegerProperty(0)

    /* テーブルのカラム幅を調整する処理クラス */
    private lateinit var stagedFileListAdjuster: TableColumnAdjuster
    private lateinit var changedFileListAdjuster: TableColumnAdjuster

    //初期化
    fun initialize() {
        stagedFileTypeColumn.cellValueFactory = PropertyValueFactory("type")
        stagedFilePathColumn.cellValueFactory = PropertyValueFactory("path")

        changedFileTypeColumn.cellValueFactory = PropertyValueFactory("type")
        changedFilePathColumn.cellValueFactory = PropertyValueFactory("path")

        stagedFileTopBox.style = CSS.topBoxStyle
        stagedFileList.style = CSS.fileListStyle
        stagedFileBottomBox.style = CSS.bottomBoxStyle
        stagedFileTypeColumn.style = CSS.typeColumnStyle
        stagedFilePathColumn.style = CSS.pathColumnStyle

        changedFileTopBox.style = CSS.topBoxStyle
        changedFileList.style = CSS.fileListStyle
        changedFileBottomBox.style = CSS.bottomBoxStyle
        changedFileTypeColumn.style = CSS.typeColumnStyle
        changedFilePathColumn.style = CSS.pathColumnStyle

        stagedFileListAdjuster = TableColumnAdjuster(stagedFileList, stagedFilePathColumn)
        changedFileListAdjuster = TableColumnAdjuster(changedFileList, changedFilePathColumn)

        stageButton.disableProperty().bind(changedFileNumber.isEqualTo(0))
        unStageButton.disableProperty().bind(stagedFileNumber.isEqualTo(0))
        commitButton.disableProperty().bind(stagedFileNumber.isEqualTo(0))

        commitButton.setOnAction { WorkTreeMenu.command.onCommitMenu() }
        unStageButton.setOnAction { WorkTreeMenu.command.onUnStageMenu() }
        stageButton.setOnAction { WorkTreeMenu.command.onStageMenu() }
    }

    //表示完了時にListenerを設定する
    override fun displayCompleted() {
        CommitInfo.controller.headerDataProperty.addListener { _, _, newValue ->
            if(newValue != null) {
                updateStagedFiles(newValue.stagedFiles)
                updateChangedFiles(newValue.changedFiles)
                newValue.stagedFilesProperty.addListener { _, _, newVal -> updateStagedFiles(newVal) }
                newValue.changedFilesProperty.addListener { _, _, newVal -> updateChangedFiles(newVal) }
            } else {
                stagedFileList.selectionModel.clearSelection()
                changedFileList.selectionModel.clearSelection()
            }
        }

        stagedFileList.selectionModel.selectedItemProperty().addListener { _, _, entry ->
            CommitDiff.controller.selectDiffEntry(entry?.diffEntry)
        }
        changedFileList.selectionModel.selectedItemProperty().addListener { _, _, entry ->
            CommitDiff.controller.selectDiffEntry(entry?.diffEntry)
        }

        stagedFileListAdjuster.adjustColumnWidth()
        changedFileListAdjuster.adjustColumnWidth()
    }

    private fun updateStagedFiles(files: List<GviewGitFileEntryModel>?) {
        if(files != null) {
            stagedFileList.items.setAll(files.map { RowData(it) })
        } else {
            stagedFileList.items.clear()
        }
        stagedFileNumber.value = stagedFileList.items.size
    }

    private fun updateChangedFiles(files: List<GviewGitFileEntryModel>?) {
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
