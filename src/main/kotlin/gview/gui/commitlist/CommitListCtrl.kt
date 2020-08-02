package gview.gui.commitlist

import gview.getCurrentRepository
import gview.gui.framework.BaseCtrl
import gview.gui.util.getVScrollBar
import gview.model.GviewCommitListModel
import gview.model.GviewHeadFilesModel
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*

class CommitListCtrl: BaseCtrl() {

    //FXML内のエレメント選言
    @FXML private lateinit var commitListTable: TableView<RowData>
    @FXML private lateinit var treeColumn: TableColumn<RowData, CellData>
    @FXML private lateinit var infoColumn: TableColumn<RowData, CellData>

    //行データ(インターフェース)
    interface RowData {
        val styleClassName: String
        val treeCellValue: CellData
        val infoCellValue: CellData
    }
    //カラム単位データの基本クラス
    open class CellData {
        open fun update(tableCell: Cell): Pair<Node?, String?> { return Pair(null, null) }
        open fun layout(tableCell: Cell) {}
    }

    //コミットテーブル用セルファクトリクラス
    //行データとカラムデータを受けて描画処理をコールする
    class Cell: TableCell<RowData, CellData>() {

        //カラム情報 - updateItemで受信する
        private var cellData: CellData? = null

        //データ更新通知
        override fun updateItem(data: CellData?, empty: Boolean) {
            super.updateItem(data, empty)
            cellData = data
            val pair = if(data != null && !empty) { data.update(this ) } else { Pair(null, null) }
            graphic = pair.first
            text = pair.second
        }

        //描画コンポーネント配置通知
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

    //レーン幅が大きくなり過ぎると表示が壊れるので、制限する
    private fun treeColumnMaxWidth(laneNumber: Int) = treeColumnWidth(laneNumber) * 2.0

    //最大レーン番号 ( = レーン数 - 1 )
    private var maxLaneNumber: Int = 0

    //縦スクロールバー
    var verticalScrollBar: ScrollBar? = null

    //初期化
    fun initialize() {
        treeColumn.setCellValueFactory { row -> ReadOnlyObjectWrapper<CellData>(row.value.treeCellValue) }
        infoColumn.setCellValueFactory { row -> ReadOnlyObjectWrapper<CellData>(row.value.infoCellValue) }
        treeColumn.setCellFactory { _ -> Cell() }
        infoColumn.setCellFactory { _ -> Cell() }

        /* ヘッダ行のCSS Classを設定するためにRowFactoryを更新する */
        commitListTable.setRowFactory { _ -> object : TableRow<RowData>() {
            override fun updateItem(rowData: RowData?, empty: Boolean) {
                styleClass.setAll("cell", "table-row-cell", rowData?.styleClassName)
                super.updateItem(rowData, empty)
            }
        }}

        //初期状態はinvisible
        commitListTable.isVisible = false
    }

    //表示完了時にListenerを設定する
    override fun displayCompleted() {
        //データ更新時の再表示
        val branchList = getCurrentRepository().branchList
        branchList.commits.commitListProperty.addListener { _ ->
            updateCommitList(branchList.headFiles, branchList.commits) }

        //テーブル幅変更時のカラム幅調整
        commitListTable.widthProperty().addListener { _ -> adjustLastColumnWidth() }
        //スクロールバー表示変更時のカラム幅調整
        getVScrollBar(commitListTable)?.visibleProperty()?.addListener { _ -> adjustLastColumnWidth() }
        //Treeカラム幅変更時のカラム幅調整
        treeColumn.widthProperty().addListener { _ ->
            xPitch = treeColumn.width / ( maxLaneNumber + 2 )
            adjustLastColumnWidth()
        }
        //縦スクロールバー幅変更時のカラム幅調整
        verticalScrollBar = getVScrollBar(commitListTable)
        verticalScrollBar?.widthProperty()?.addListener { _ -> adjustLastColumnWidth() }
   }

    private var headerRow : HeaderRow? = null
    private val commitRows: MutableList<CommitRow> = mutableListOf()

    //表示更新
    private fun updateCommitList(header: GviewHeadFilesModel, commits:GviewCommitListModel) {

        //最初に全削除
        commitListTable.items.clear()
        commitRows.clear()

        //ヘッダ情報業を追加
        headerRow = HeaderRow(this, header, commits.commitMap[header.headerId])
        commitListTable.items.add(headerRow)

        //コミット情報行を追加
        commits.commitListProperty.value?.forEach {
            val row = CommitRow(this, it)
            commitRows.add(row)
            commitListTable.items.add(row)
        }

        //ピッチを既定値に戻す
        xPitch = defaultXPitch

        //レーン数からカラム幅を決定する
        maxLaneNumber = commits.commitListProperty.value?.map { it.laneNumber }?.max() ?: 0
        treeColumn.maxWidth = treeColumnMaxWidth(maxLaneNumber + 1)
        treeColumn.prefWidth = treeColumnWidth(maxLaneNumber + 1)

        //リストを可視化
        commitListTable.isVisible = true
    }

    /* 縦スクロールバー表示の有無を確認した上で、カラム幅を決定する */
    private fun adjustLastColumnWidth() {
        infoColumn.prefWidth = commitListTable.width - treeColumn.width
        if(verticalScrollBar != null && verticalScrollBar!!.isVisible) {
            infoColumn.prefWidth -= verticalScrollBar!!.width
        }
    }
}