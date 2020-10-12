package gview.gui.commitlist

import gview.gui.framework.GviewBasePaneCtrl
import gview.gui.util.TableColumnAdjuster
import gview.model.commit.GviewCommitListModel
import gview.model.workfile.GviewWorkFilesModel
import gview.model.GviewRepositoryModel
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*

class CommitListCtrl
    : GviewBasePaneCtrl() {

    //FXML内のエレメント選言
    @FXML private lateinit var commitListTable: TableView<RowData>
    @FXML private lateinit var treeColumn: TableColumn<RowData, CellData>
    @FXML private lateinit var infoColumn: TableColumn<RowData, CellData>

    //選択行
    val selectedRowProperty = SimpleObjectProperty<RowData>()

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
        open val contextMenu: ContextMenu? = null
    }

    //コミットテーブル用セルファクトリクラス
    //行データとカラムデータを受けて描画処理をコールする
    class Cell: TableCell<RowData, CellData>() {

        //カラム情報 - updateItemで受信する
        private var cellData: CellData? = null

        init {
            style = CSS.cellStyle
        }

        //データ更新通知
        override fun updateItem(data: CellData?, empty: Boolean) {
            super.updateItem(data, empty)
            cellData = data
            val pair = if(data != null && !empty) { data.update(this ) } else { Pair(null, null) }
            graphic = pair.first
            text = pair.second
            contextMenu = data?.contextMenu
        }

        //描画コンポーネント配置通知
        override fun layoutChildren() {
            super.layoutChildren()
            cellData?.layout(this)
        }
    }

    companion object {
        //レーンピッチの既定値
        private const val defaultXPitch = 15.0
    }

    //レーン数からツリーカラムの幅を求める
    var xPitch = defaultXPitch
    fun treeColumnWidth(laneNumber: Int) =  xPitch * ( laneNumber + 1.0 )

    //レーン幅が大きくなり過ぎると表示が壊れるので、制限する
    private fun treeColumnMaxWidth(laneNumber: Int) = treeColumnWidth(laneNumber) * 2.0

    //最大レーン番号 ( = レーン数 - 1 )
    private var maxLaneNumber: Int = 0

    /* テーブルのカラム幅を調整する処理クラス */
    private lateinit var commitListAdjuster: TableColumnAdjuster

    //初期化
    fun initialize() {
        commitListTable.style = CSS.commitListStyle
        commitListTable.placeholder = Label("")

        treeColumn.setCellValueFactory { row -> ReadOnlyObjectWrapper(row.value.treeCellValue) }
        infoColumn.setCellValueFactory { row -> ReadOnlyObjectWrapper(row.value.infoCellValue) }
        treeColumn.setCellFactory { Cell() }
        infoColumn.setCellFactory { Cell() }

        /* 行のCSS Classを設定するためにRowFactoryを更新する */
        commitListTable.setRowFactory {
            object : TableRow<RowData>() {
                override fun updateItem(rowData: RowData?, empty: Boolean) {
                    styleClass.setAll("cell", "table-row-cell", rowData?.styleClassName)
                    super.updateItem(rowData, empty)
                }
            }
        }

        commitListAdjuster = TableColumnAdjuster(commitListTable, infoColumn)

        //初期状態はinvisible
        commitListTable.isVisible = false
    }

    //表示完了時にListenerを設定する
    override fun displayCompleted() {

        //データ更新時の再表示
        val repository = GviewRepositoryModel.currentRepository
        val headers = repository.workFileInfo
        val commits = repository.commits

        //コミット情報が更新されたら再描画
        commits.addListener { update(headers, commits) }

        //行選択変更時
        commitListTable.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            selectedRowProperty.value = newValue
        }

        //Treeカラム幅変更時の描画更新
        treeColumn.widthProperty().addListener { _ ->
            xPitch = treeColumn.width / ( maxLaneNumber + 2 )
        }

        //カラム幅の調整
        commitListAdjuster.adjustColumnWidth()
    }

    //表示更新
    private fun update(
            header: GviewWorkFilesModel,
            commits: GviewCommitListModel) {

        //最初に全削除
        commitListTable.items.clear()

        val headerLaneNumber = commits.headerLaneNumber ?: 0

        //ヘッダ情報業を追加
        if(commits.headId != null) {
            val commitData = commits.commitIdMap[commits.headId]
            if(commitData != null && commitData.localBranches.find { it.selected } != null) {
                commitListTable.items.add(HeaderRowData(this, header, headerLaneNumber, commitData))
            }
        }

        //コミット情報行を追加
        commits.commitList.forEach {
            commitListTable.items.add(CommitRowData(this, it))
        }

        //ピッチを既定値に戻す
        xPitch = defaultXPitch

        //レーン数からカラム幅を決定する
        if(commits.commitList.isNotEmpty()) {
            maxLaneNumber = commits.commitList.maxOf { it.laneNumber }
            if (maxLaneNumber < headerLaneNumber) maxLaneNumber = headerLaneNumber
        } else {
            maxLaneNumber = 0
        }
        treeColumn.maxWidth = treeColumnMaxWidth(maxLaneNumber + 1)
        treeColumn.prefWidth = treeColumnWidth(maxLaneNumber + 1)

        //戦闘のコミットまたはヘッダを選択する
        commitListTable.selectionModel.select(if(commitListTable.items.size <= 1) 0 else 1)

        //リストを可視化
        commitListTable.isVisible = true
    }

    private object CSS {
        val cellStyle = """
            -fx-border-width: 0;
            -fx-padding: 0;
        """.trimIndent()

        val commitListStyle = """
            -fx-padding: 0;
        """.trimIndent()
    }
}
