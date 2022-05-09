package gview.view.commitlist

import gview.view.util.GvTextMessage
import gview.model.workfile.GvWorkFilesModel
import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.control.ContextMenu
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import java.text.DateFormat
import java.util.*

class HeaderRowData(
    commitList: CommitListCtrl,
    val model: GvWorkFilesModel,
    val laneNumber: Int?)
    : AbstractRowData() {

    override val treeCellValue: CommitListCtrl.CellData = HeaderTreeCellData(commitList, model)
    override val infoCellValue: CommitListCtrl.CellData = HeaderInfoCellData(model)

    override val styleClassName: String = "header-row"

    //コミットツリーセル
    inner class HeaderTreeCellData(
            private val commitList: CommitListCtrl,
            val model: GvWorkFilesModel)
        : CommitListCtrl.CellData {

        override fun update(tableCell: CommitListCtrl.Cell): Pair<Node?, String?> {
            return Pair(null, null)
        }

        override fun layout(tableCell: CommitListCtrl.Cell) {

            val canvas = Canvas(tableCell.width, tableCell.height)
            val ys = 0.0
            val ye = tableCell.height
            drawMark(canvas, ys, ye)
            tableCell.graphic = Pane(canvas)
            tableCell.text = null
        }

        override val contextMenu: ContextMenu? = null

        private fun drawMark(
                canvas: Canvas,
                ys: Double,
                ye: Double) {

            val gc = setColor(canvas, laneNumber ?: 0)
            val x = commitList.treeColumnWidth(laneNumber ?: 0)
            val y = (ye - ys) / 2.0
            val xr = 7.0
            val yr = 7.0
            gc.fillRect(x - xr, y - yr, xr * 2.0, yr * 2.0)
            if(laneNumber != null) gc.strokeLine(x, y, x, ye)
        }
    }

    //コミット情報セル
    inner class HeaderInfoCellData(
            private val model: GvWorkFilesModel)
        : CommitListCtrl.CellData {

        override fun update(tableCell: CommitListCtrl.Cell)
                : Pair<Node?, String?>  {

            val timeStamp = DateFormat.getDateTimeInstance().format(Date())
            val row1 = GvTextMessage("ワークツリー情報 - ", timeStamp)
            val row2 = GvTextMessage("ステージ済:", "${model.stagedFiles.size} ファイル")
            val row3 = GvTextMessage("ステージ未:", "${model.changedFiles.size} ファイル")
            return Pair(VBox(row1, row2, row3), null)
        }

        override fun layout(tableCell: CommitListCtrl.Cell) {
        }

        override val contextMenu: ContextMenu? = null
    }

}