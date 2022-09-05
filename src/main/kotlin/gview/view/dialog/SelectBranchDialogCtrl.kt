package gview.view.dialog

import gview.model.branch.GvLocalBranch
import gview.view.framework.GvCustomDialogCtrl
import gview.view.util.GvColumnAdjuster
import javafx.beans.property.SimpleBooleanProperty
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory

class SelectBranchDialogCtrl(private val branches: List<GvLocalBranch>): GvCustomDialogCtrl() {

    @FXML private lateinit var fileList: TableView<RowData>
    @FXML private lateinit var nameColumn: TableColumn<RowData, String>
    @FXML private lateinit var localPathColumn: TableColumn<RowData, String>
    @FXML private lateinit var remotePathColumn: TableColumn<RowData, String>
    @FXML private lateinit var fileCheckColumn: TableColumn<RowData, Boolean>
    @FXML private lateinit var selAllCheckBox: CheckBox

    val btnOkDisable = SimpleBooleanProperty(false)

    val selectedFiles get() = fileList.items.filter { it.check.value }.map { it.branch }

    private lateinit var fileListAdjuster: GvColumnAdjuster

    class RowData(val branch: GvLocalBranch) {
        val branchName get() = branch.name
        val localPath get() = branch.localPath
        val remotePath get() = branch.remotePath
        val check = SimpleBooleanProperty(branch.isCurrentBranch)
    }

    override fun initialize() {
        nameColumn.cellValueFactory = PropertyValueFactory("branchName")            // NON-NLS
        localPathColumn.cellValueFactory = PropertyValueFactory("localPath")        // NON-NLS
        remotePathColumn.cellValueFactory = PropertyValueFactory("remotePath")      // NON-NLS
        fileCheckColumn.cellFactory = CheckBoxTableCell.forTableColumn { index -> fileList.items[index].check }

        fileList.items.addAll(branches.map { RowData(it) })
        fileList.selectionModel.select(fileList.items.indexOfFirst { it.check.value })

        selAllCheckBox.selectedProperty().addListener { _, _, newValue ->
            fileList.items.forEach { it.check.value = newValue }
        }

        fileListAdjuster = GvColumnAdjuster(fileList, remotePathColumn)
    }
}