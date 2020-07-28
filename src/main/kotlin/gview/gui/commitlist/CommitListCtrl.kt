package gview.gui.commitlist

import gview.getCurrentRepository
import gview.gui.framework.BaseCtrl
import gview.gui.util.getVScrollBar
import gview.model.commit.CommitDataModel
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView

class CommitListCtrl: BaseCtrl() {

    @FXML private lateinit var commitListTable: TableView<RowData>
    @FXML private lateinit var treeColumn: TableColumn<RowData, CellData>
    @FXML private lateinit var infoColumn: TableColumn<RowData, CellData>

    interface RowData {
        val treeCellValue: CellData
        val infoCellValue: CellData
    }
    open class CellData {
        open fun update(cell: Cell) {}
        open fun layout(cell: Cell) {}
    }

    class Cell: TableCell<RowData, CellData>() {

        private var cellData: CellData? = null

        init {
            this.style = """
                -fx-border-style: none;
                -fx-border-width: 0;
                -fx-padding: 0;
            """.trimIndent()
        }

        override fun updateItem(data: CellData?, empty: Boolean) {
            super.updateItem(data, empty)
            cellData = if(data != null && !empty) data else null
            cellData?.update(this)
        }

        override fun layoutChildren() {
            super.layoutChildren()
            cellData?.layout(this)
        }
    }

    companion object {
        //レーンピッチの既定値
        private const val defaultXPitch = 12.0
    }

    //レーン数からツリーカラムの幅を求める
    var xPitch = defaultXPitch
    fun treeColumnWidth(laneNumber: Int) =  xPitch * ( laneNumber + 1.0 )
    fun treeColumnMaxWidth(laneNumber: Int) = treeColumnWidth(laneNumber) * 2.0

    //最大レーン番号 ( = レーン数 - 1 )
    var maxLaneNumber: Int = 0

    //初期化
    fun initialize() {
        treeColumn.setCellValueFactory { row -> ReadOnlyObjectWrapper<CellData>(row.value.treeCellValue) }
        infoColumn.setCellValueFactory { row -> ReadOnlyObjectWrapper<CellData>(row.value.infoCellValue) }
        treeColumn.setCellFactory { _ -> Cell() }
        infoColumn.setCellFactory { _ -> Cell() }
        commitListTable.isVisible = false
    }

    //表示完了時にListenerを設定する
    override fun displayCompleted() {
        //データ更新時の再表示
        getCurrentRepository().branchList.commits.commitListProperty.addListener { _, _, newVal -> update(newVal) }

        //幅変更時のカラム幅調整
        commitListTable.widthProperty().addListener { _ -> adjustLastColumnWidth() }

        //スクロールバー表示変更時のカラム幅調整
        getVScrollBar(commitListTable)?.visibleProperty()?.addListener { _ -> adjustLastColumnWidth() }

        treeColumn.widthProperty().addListener { _ ->
            xPitch = treeColumn.width / ( maxLaneNumber + 2 )
            adjustLastColumnWidth()
        }
    }

    //表示更新
    private fun update(commitList: List<CommitDataModel>) {
        //最初に全削除
        commitListTable.items.clear()

        //コミット情報行を追加
        commitList.forEach { commitListTable.items.add(CommitRow(this, it)) }

        if(!commitListTable.items.isEmpty()) {
            //レーン数からカラム幅を決定する
            maxLaneNumber = commitList.map { it.laneNumber }.max() ?: 0
            treeColumn.prefWidth = treeColumnWidth(maxLaneNumber + 1)
            treeColumn.maxWidth = treeColumnMaxWidth(maxLaneNumber + 1)
            adjustLastColumnWidth()
            //リストを表示
            commitListTable.isVisible = true
        } else {
            //リスト非表示
            commitListTable.isVisible = false
        }
    }

    /* 縦スクロールバー表示の有無を確認した上で、カラム幅を決定する */
    private fun adjustLastColumnWidth() {
        infoColumn.prefWidth = commitListTable.width - treeColumn.width - 2.0
        val vs = getVScrollBar(commitListTable)
        if(vs != null && vs.isVisible) {
            infoColumn.prefWidth -= vs.width
        }
    }
}