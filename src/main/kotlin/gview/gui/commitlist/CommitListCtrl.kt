package gview.gui.commitlist

import gview.gui.framework.BaseCtrl
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView

class CommitListCtrl: BaseCtrl() {

    @FXML private lateinit var commitListTable: TableView<RowData>
    @FXML private lateinit var treeColumn: TableColumn<RowData, TreeCellData>
    @FXML private lateinit var infoColumn: TableColumn<RowData, InfoCellData>

    interface RowData
    interface TreeCellData
    interface InfoCellData

    fun initialize() {

    }
}