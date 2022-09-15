package gview.view.dialog

import gview.model.GvRepository
import gview.view.framework.GvCustomDialogCtrl
import gview.view.util.GvColumnAdjuster
import gview.model.GvCommitFile
import javafx.beans.property.SimpleBooleanProperty
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory

class SelectChangedFilesDialogCtrl: GvCustomDialogCtrl() {
    @FXML private lateinit var selAllCheckBox: CheckBox
    @FXML private lateinit var fileList: TableView<RowData>
    @FXML private lateinit var fileTypeColumn: TableColumn<RowData, String>
    @FXML private lateinit var filePathColumn: TableColumn<RowData, String>
    @FXML private lateinit var fileCheckColumn: TableColumn<RowData, Boolean>

    class RowData(val diffEntry: GvCommitFile) {
        val type: String = diffEntry.typeName
        val path: String = diffEntry.path
        val check = SimpleBooleanProperty(true)
    }

    /* テーブルのカラム幅を調整する処理クラス */
    private lateinit var fileListAdjuster: GvColumnAdjuster

    //初期化
    override fun initialize() {
        fileList.styleClass.add("FileList")                 // NON-NLS
        fileTypeColumn.cellValueFactory = PropertyValueFactory("type")      // NON-NLS
        filePathColumn.cellValueFactory = PropertyValueFactory("path")      // NON-NLS
        fileCheckColumn.cellFactory = CheckBoxTableCell.forTableColumn { index -> fileList.items[index].check }

        fileTypeColumn.styleClass.add("FileType")           // NON-NLS
        filePathColumn.styleClass.add("FilePath")           // NON-NLS
        fileCheckColumn.styleClass.add("FileCheck")         // NON-NLS

        val currentRepository = GvRepository.currentRepository
        if(currentRepository != null) {
            val files = currentRepository.workFiles.changedFiles.value
            fileList.items.addAll(files.map { RowData(it) })
        }
        selAllCheckBox.selectedProperty().addListener { _, _, newValue ->
            fileList.items.forEach { it.check.value= newValue }
        }

        fileListAdjuster = GvColumnAdjuster(fileList, filePathColumn)
    }

    //選択されたファイル一覧
    val selectedFiles: List<GvCommitFile> get() =
        fileList.items.filter { it.check.value }.map { it.diffEntry }
}
