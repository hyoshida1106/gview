package gview.gui.commitlist

import gview.getCurrentRepository
import gview.gui.framework.BaseCtrl
import gview.model.commit.CommitDataModel
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView

class CommitListCtrl: BaseCtrl() {

    @FXML private lateinit var commitListTable: TableView<RowData>
    @FXML private lateinit var treeColumn: TableColumn<RowData, TreeCellData>
    @FXML private lateinit var infoColumn: TableColumn<RowData, InfoCellData>

    interface RowData {
        val treeCellValue: TreeCellData
        val infoCellValue: InfoCellData
    }
    interface TreeCellData
    interface InfoCellData

    //初期化
    fun initialize() {
        treeColumn.setCellValueFactory { row -> ReadOnlyObjectWrapper<TreeCellData>(row.value.treeCellValue) }
        infoColumn.setCellValueFactory { row -> ReadOnlyObjectWrapper<InfoCellData>(row.value.infoCellValue) }
        treeColumn.setCellFactory { _ -> TreeCell() }
        infoColumn.setCellFactory { _ -> InfoCell() }
        commitListTable.isVisible = false
    }

    //表示完了時にListenerを設定する
    override fun displayCompleted() {
        val commits = getCurrentRepository().branchList.commits
        commits.commitListProperty.addListener { _, _, newVal -> update(newVal) }
    }

    private fun update(commitList: List<CommitDataModel>) {
        //最初に全削除
        commitListTable.items.clear()

        commitListTable.isVisible = true
    }
}