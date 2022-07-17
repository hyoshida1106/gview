package gview.view.commitlist

import gview.model.GvRepository
import gview.view.framework.GvBaseWindowCtrl
import gview.view.util.GvColumnAdjuster
import gview.model.commit.GvCommitList
import gview.model.workfile.GvWorkFileList
import javafx.application.Platform
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*

class CommitListCtrl: GvBaseWindowCtrl() {
    @FXML private lateinit var commitListTable: TableView<RowData>
    @FXML private lateinit var treeColumn: TableColumn<RowData, CellData>
    @FXML private lateinit var infoColumn: TableColumn<RowData, CellData>

    val selectedRowProperty = SimpleObjectProperty<RowData>()

    interface RowData {
        val styleClassName: String
        val treeCellValue: CellData
        val infoCellValue: CellData
    }

    interface CellData {
        fun update(): Pair<Node?, String?>
        fun layout(tableCell: Cell)
        val contextMenu: ContextMenu?
    }

    class Cell: TableCell<RowData, CellData>() {
        private var cellData: CellData? = null

        override fun updateItem(data: CellData?, empty: Boolean) {
            super.updateItem(data, empty)
            cellData = data
            if(data != null && !empty) {
                val( graphic, text ) = data.update()
                this.graphic = graphic
                this.text = text
            } else {
                this.graphic = null
                this.text = null
            }
            contextMenu = data?.contextMenu
        }

        override fun layoutChildren() {
            super.layoutChildren()
            cellData?.layout(this)
        }
    }

    private val defaultXPitch = 12.0

    var xPitch = defaultXPitch

    fun treeColumnWidth(laneNumber: Int) =  xPitch * ( laneNumber + 1.0 )

    //レーン幅が大きくなり過ぎると表示が壊れるので、制限する
    private fun treeColumnMaxWidth(laneNumber: Int) = treeColumnWidth(laneNumber) * 2.0

    //最大レーン番号 ( = レーン数 - 1 )
    private var maxLaneNumber: Int = 0

    /* テーブルのカラム幅を調整する処理クラス */
    private lateinit var commitListAdjuster: GvColumnAdjuster

    fun initialize() {
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

        commitListAdjuster = GvColumnAdjuster(commitListTable, infoColumn)

        GvRepository.currentRepositoryProperty.addListener { _, _, repository ->
            Platform.runLater { updateRepository(repository) }
        }

        //初期状態はinvisible
        commitListTable.isVisible = false
    }

    private fun updateRepository(repository: GvRepository) {
        val headers = repository.workFiles
        val commits = repository.commits
        update(headers, commits)
        commits.commitList.addListener { _, _, _ ->
            Platform.runLater { update(headers, commits) } }
    }

    //表示完了時にListenerを設定する
    override fun displayCompleted() {
        //行選択変更時
        commitListTable.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            selectedRowProperty.value = newValue
        }
        //Treeカラム幅変更時の描画更新
        treeColumn.widthProperty().addListener { _ ->
            xPitch = treeColumn.width / (maxLaneNumber + 2)
        }
        //カラム幅の調整
        commitListAdjuster.adjustColumnWidth()
    }

    //表示更新
    private fun update(
        header: GvWorkFileList,
        commits: GvCommitList) {

        //最初に全削除
        commitListTable.items.clear()

        //ヘッダ情報業を追加
        val headerLaneNumber = commits.headerLaneNumber
        commitListTable.items.add(HeaderRowData(this, header, headerLaneNumber))

        //コミット情報行を追加
        commits.commitList.value.forEach {
            commitListTable.items.add(CommitRowData(this, it))
        }

        //ピッチを既定値に戻す
        xPitch = defaultXPitch

        //レーン数からカラム幅を決定する
        if(commits.commitList.value.isNotEmpty()) {
            maxLaneNumber = commits.commitList.value.maxOf { it.laneNumber }
            if (headerLaneNumber != null && maxLaneNumber < headerLaneNumber) {
                maxLaneNumber = headerLaneNumber
            }
        } else {
            maxLaneNumber = 0
        }
        treeColumn.maxWidth = treeColumnMaxWidth(maxLaneNumber + 1)
        treeColumn.prefWidth = treeColumnWidth(maxLaneNumber + 1)

        //先頭のコミットまたはヘッダを選択する
        commitListTable.selectionModel.select(if(commitListTable.items.size <= 1) 0 else 1)

        //リストを可視化
        commitListTable.isVisible = true
    }
}
