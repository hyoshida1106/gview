package gview.gui.dialog

import gview.gui.commitinfo.CommitInfo
import gview.gui.framework.GviewDialog
import gview.gui.framework.GviewDialogController
import gview.gui.util.TableColumnAdjuster
import gview.model.commit.GviewGitFileEntryModel
import javafx.beans.property.SimpleBooleanProperty
import javafx.fxml.FXML
import javafx.scene.control.ButtonType
import javafx.scene.control.CheckBox
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory

class SelectUnStageFilesDialog: GviewDialog<SelectUnStageFilesDialogCtrl>(
        "アンステージするファイルを選択してください",
        "/view/SelectUnStageFilesDialogView.fxml",
        ButtonType.OK, ButtonType.CANCEL) {
    val selectedFiles: List<GviewGitFileEntryModel> get() = controller.selectedFiles
}

class SelectUnStageFilesDialogCtrl : GviewDialogController() {

    @FXML private lateinit var selAllCheckBox: CheckBox
    @FXML private lateinit var fileList: TableView<RowData>
    @FXML private lateinit var fileTypeColumn: TableColumn<RowData, String>
    @FXML private lateinit var filePathColumn: TableColumn<RowData, String>
    @FXML private lateinit var fileCheckColumn: TableColumn<RowData, Boolean>

    class RowData(val diffEntry: GviewGitFileEntryModel) {
        val type: String = diffEntry.typeName
        val path: String = diffEntry.path
        val check = SimpleBooleanProperty(false)
    }

    /* テーブルのカラム幅を調整する処理クラス */
    private lateinit var fileListAdjuster: TableColumnAdjuster

    //初期化
    override fun initialize() {

        fileList.selectionModel = null
        fileList.style = CSS.fileListStyle

        fileTypeColumn.cellValueFactory = PropertyValueFactory<RowData, String>("type")
        filePathColumn.cellValueFactory = PropertyValueFactory<RowData, String>("path")
        fileCheckColumn.cellFactory = CheckBoxTableCell.forTableColumn { index -> fileList.items[index].check }

        fileTypeColumn.style = CSS.fileTypeStyle
        filePathColumn.style = CSS.filePathStyle
        fileCheckColumn.style = CSS.fileCheckStyle

        val files = CommitInfo.controller.headerData?.stagedFiles
        if (files != null) {
            fileList.items.addAll(files.map { RowData(it) })
        }

        selAllCheckBox.selectedProperty().addListener { _, _, newValue ->
            fileList.items.forEach { it.check.value= newValue }
        }

        fileListAdjuster = TableColumnAdjuster(fileList, filePathColumn)
    }

    //選択されたファイル一覧
    val selectedFiles: List<GviewGitFileEntryModel> get() =
        fileList.items.filter { it.check.value }.map { it.diffEntry }

    //CSSスタイル定義
    private object CSS {
        val fileListStyle = """ 
        """.trimIndent()
        val fileTypeStyle = """
            -fx-alignment: CENTER;
        """.trimIndent()
        val filePathStyle = """
        """.trimIndent()
        val fileCheckStyle = """
            -fx-alignment: CENTER;
        """.trimIndent()
    }
}
