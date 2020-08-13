package gview.gui.commitinfo

import gview.gui.MainView
import gview.gui.framework.BaseCtrl
import gview.gui.util.TableColumnAdjuster
import gview.model.commit.GviewGitFileEntryModel
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.AnchorPane

class HeaderFileListCtrl: BaseCtrl() {

    @FXML private lateinit var stagedFileTopBox: AnchorPane
    @FXML private lateinit var stagedFileList: TableView<RowData>
    @FXML private lateinit var stagedFileBottomBox: AnchorPane
    @FXML private lateinit var stagedFileTypeColumn: TableColumn<RowData, String>
    @FXML private lateinit var stagedFilePathColumn: TableColumn<RowData, String>
    @FXML private lateinit var stagedFileCheckColumn: TableColumn<RowData, Boolean>

    @FXML private lateinit var changedFileTopBox: AnchorPane
    @FXML private lateinit var changedFileList: TableView<RowData>
    @FXML private lateinit var changedFileBottomBox: AnchorPane
    @FXML private lateinit var changedFileTypeColumn: TableColumn<RowData, String>
    @FXML private lateinit var changedFilePathColumn: TableColumn<RowData, String>
    @FXML private lateinit var changedFileCheckColumn: TableColumn<RowData, Boolean>

    @FXML private lateinit var stageButton: Button
    @FXML private lateinit var unStageButton: Button
    @FXML private lateinit var commitButton: Button

    /* テーブルデータ */
    class RowData(val diffEntry: GviewGitFileEntryModel) {
        val type: String = diffEntry.typeName
        val path: String = diffEntry.path
        val check = SimpleBooleanProperty(false)
    }

    /* テーブルのカラム幅を調整する処理クラス */
    private lateinit var stagedFileListAdjuster:  TableColumnAdjuster
    private lateinit var changedFileListAdjuster: TableColumnAdjuster

    //初期化
    fun initialize() {
        stagedFileTypeColumn.cellValueFactory = PropertyValueFactory<RowData, String>("type")
        stagedFilePathColumn.cellValueFactory = PropertyValueFactory<RowData, String>("path")
        stagedFileCheckColumn.cellValueFactory = PropertyValueFactory<RowData, Boolean>("check")
        stagedFileCheckColumn.setCellFactory {
            val cell = CheckBoxTableCell<RowData, Boolean>()
            cell.setSelectedStateCallback { index -> stagedFileList.items[index].check }
            cell
        }

        changedFileTypeColumn.cellValueFactory = PropertyValueFactory<RowData, String>("type")
        changedFilePathColumn.cellValueFactory = PropertyValueFactory<RowData, String>("path")
        changedFileCheckColumn.cellValueFactory = PropertyValueFactory<RowData, Boolean>("check")
        changedFileCheckColumn.setCellFactory {
            val cell = CheckBoxTableCell<RowData, Boolean>()
            cell.setSelectedStateCallback { index -> changedFileList.items[index].check }
            cell
        }

        stagedFileTopBox.style = CSS.topBoxStyle
        stagedFileList.style = CSS.fileListStyle
        stagedFileBottomBox.style = CSS.bottomBoxStyle
        stagedFileTypeColumn.style = CSS.typeColumnStyle
        stagedFilePathColumn.style = CSS.pathColumnStyle
        stagedFileCheckColumn.style = CSS.checkColumnStyle

        changedFileTopBox.style = CSS.topBoxStyle
        changedFileList.style = CSS.fileListStyle
        changedFileBottomBox.style = CSS.bottomBoxStyle
        changedFileTypeColumn.style = CSS.typeColumnStyle
        changedFilePathColumn.style = CSS.pathColumnStyle
        changedFileCheckColumn.style = CSS.checkColumnStyle

        stagedFileListAdjuster = TableColumnAdjuster(stagedFileList, stagedFilePathColumn)
        changedFileListAdjuster = TableColumnAdjuster(changedFileList, changedFilePathColumn)
    }

    //表示完了時にListenerを設定する
    override fun displayCompleted() {
        CommitInfoView.controller.headerDataProperty.addListener { _, _, newValue ->
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
            CommitDiffView.controller.selectDiffEntry(entry?.diffEntry)
        }
        changedFileList.selectionModel.selectedItemProperty().addListener { _, _, entry ->
            CommitDiffView.controller.selectDiffEntry(entry?.diffEntry)
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
    }

    private fun updateChangedFiles(files: List<GviewGitFileEntryModel>?) {
        if(files != null) {
            changedFileList.items.setAll(files.map { RowData(it) })
        } else {
            changedFileList.items.clear()
        }
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
            -fx-padding: 0 10 0 10;
            -fx-background-color: -background-color;
        """.trimIndent()

        val typeColumnStyle = """
            -fx-alignment: CENTER;
        """.trimIndent()

        val pathColumnStyle = """
        """.trimIndent()

        val checkColumnStyle = """
            -fx-alignment: CENTER;
}        """.trimIndent()
    }
}
