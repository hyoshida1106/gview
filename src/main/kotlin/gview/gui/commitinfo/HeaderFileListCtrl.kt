package gview.gui.commitinfo

import gview.gui.MainView
import gview.gui.framework.BaseCtrl
import gview.gui.util.TableColumnAdjuster
import gview.model.commit.GviewGitFileEntryModel
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory

class HeaderFileListCtrl: BaseCtrl() {

    @FXML private lateinit var stagedFileLabel: Label
    @FXML private lateinit var stagedFileList: TableView<RowData>
    @FXML private lateinit var stagedFileTypeColumn: TableColumn<RowData, String>
    @FXML private lateinit var stagedFilePathColumn: TableColumn<RowData, String>

    @FXML private lateinit var changedFileLabel: Label
    @FXML private lateinit var changedFileList: TableView<RowData>
    @FXML private lateinit var changedFileTypeColumn: TableColumn<RowData, String>
    @FXML private lateinit var changedFilePathColumn: TableColumn<RowData, String>

    /* テーブルデータ */
    class RowData(val diffEntry: GviewGitFileEntryModel) {
        val type: String = diffEntry.typeName
        val path: String = diffEntry.path
    }

    /* テーブルのカラム幅を調整する処理クラス */
    private lateinit var stagedFileListAdjuster:  TableColumnAdjuster
    private lateinit var changedFileListAdjuster: TableColumnAdjuster

    //初期化
    fun initialize() {
        stagedFileTypeColumn.cellValueFactory = PropertyValueFactory<RowData, String>("type")
        stagedFilePathColumn.cellValueFactory = PropertyValueFactory<RowData, String>("path")
        changedFileTypeColumn.cellValueFactory = PropertyValueFactory<RowData, String>("type")
        changedFilePathColumn.cellValueFactory = PropertyValueFactory<RowData, String>("path")

        stagedFileLabel.style = CSS.labelStyle
        stagedFileList.style = CSS.fileListStyle
        stagedFileTypeColumn.style = CSS.typeColumnStyle
        stagedFilePathColumn.style = CSS.pathColumnStyle

        changedFileLabel.style = CSS.labelStyle
        changedFileList.style = CSS.fileListStyle
        changedFileTypeColumn.style = CSS.typeColumnStyle
        changedFilePathColumn.style = CSS.pathColumnStyle

        stagedFileListAdjuster = TableColumnAdjuster(stagedFileList, stagedFilePathColumn)
        changedFileListAdjuster = TableColumnAdjuster(changedFileList, changedFilePathColumn)
    }

    //表示完了時にListenerを設定する
    override fun displayCompleted() {
        val headFiles = MainView.controller.repository.headFiles
        headFiles.stagedFilesProperty.addListener  { _, _, newValue -> updateStagedFiles(newValue)  }
        headFiles.changedFilesProperty.addListener { _, _, newValue -> updateChangedFiles(newValue) }

        stagedFileList.selectionModel.selectedItemProperty().addListener { _, _, entry ->
            CommitDiffView.controller.selectDiffEntry(entry?.diffEntry)
        }
        changedFileList.selectionModel.selectedItemProperty().addListener { _, _, entry ->
            CommitDiffView.controller.selectDiffEntry(entry?.diffEntry)
        }

        stagedFileListAdjuster.adjustColumnWidth()
        changedFileListAdjuster.adjustColumnWidth()
    }

    private fun updateStagedFiles(files: List<GviewGitFileEntryModel>) {
        stagedFileList.items.setAll( files.map {RowData(it) })
    }

    private fun updateChangedFiles(files: List<GviewGitFileEntryModel>) {
        changedFileList.items.setAll( files.map {RowData(it) })
    }

    object CSS {
        val labelStyle = """
            -fx-padding: 8 10 0 10;
            -fx-background-color: -background-color;
        """.trimIndent()

        val fileListStyle = """
            -fx-padding: 7 8 7 8;
            -fx-background-color: -background-color;
        """.trimIndent()

        val typeColumnStyle = """
            -fx-alignment: CENTER;
        """.trimIndent()

        val pathColumnStyle = """
        """.trimIndent()
    }
}
