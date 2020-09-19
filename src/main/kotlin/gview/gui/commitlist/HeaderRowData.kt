package gview.gui.commitlist

import gview.gui.util.textMessage
import gview.model.GviewHeadFilesModel
import gview.model.commit.GviewCommitDataModel
import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import java.text.DateFormat
import java.util.*

class HeaderRowData(commitList: CommitListCtrl, val model: GviewHeadFilesModel, head: GviewCommitDataModel?)
    : AbstractRowData() {

    override val treeCellValue: CommitListCtrl.CellData = HeaderTreeCellData(commitList, model)
    override val infoCellValue: CommitListCtrl.CellData = HeaderInfoCellData(model)

    override val styleClassName: String = "header-row"

    //コミットツリーセル
    inner class HeaderTreeCellData(private val commitList: CommitListCtrl,
                                   val model: GviewHeadFilesModel) : CommitListCtrl.CellData() {

        override fun layout(tableCell: CommitListCtrl.Cell) {
            val canvas = Canvas(tableCell.width, tableCell.height)
            val ys = 0.0
            val ye = tableCell.height
            drawMark(canvas, ys, ye)
            tableCell.graphic = Pane(canvas)
            tableCell.text = null
        }

        private fun drawMark(canvas: Canvas, ys: Double, ye: Double) {
            val gc = setColor(canvas, laneNumber)
            val x = commitList.treeColumnWidth(laneNumber)
            val y = (ye - ys) / 2.0
            val xr = 7.0
            val yr = 7.0
            gc.fillRect(x - xr, y - yr, xr * 2.0, yr * 2.0)
            gc.strokeLine(x, y, x, ye)
        }
    }

    //コミット情報セル
    inner class HeaderInfoCellData(private val model: GviewHeadFilesModel): CommitListCtrl.CellData() {
        override fun update(tableCell: CommitListCtrl.Cell): Pair<Node?, String?>  {
            val timeStamp = DateFormat.getDateTimeInstance().format(Date())
            val row1 = textMessage("ワークツリー情報 - ", timeStamp)
            val row2 = textMessage("ステージ済:", "${model.stagedFiles.size} ファイル")
            val row3 = textMessage("ステージ未:", "${model.changedFiles.size} ファイル")
            return Pair(VBox(row1, row2, row3), null)
        }
    }

    //レーン番号
    val laneNumber: Int = head?.laneNumber?: 0
}