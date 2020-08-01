package gview.gui.commitlist

import gview.model.GviewHeadFilesModel
import gview.model.commit.GviewCommitDataModel
import javafx.scene.canvas.Canvas
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Paint
import java.text.DateFormat
import java.util.*

class HeaderRow(commitList: CommitListCtrl, model: GviewHeadFilesModel, head: GviewCommitDataModel?): BaseRow() {

    override val treeCellValue: CommitListCtrl.CellData = HeaderTreeCellData(commitList, model)
    override val infoCellValue: CommitListCtrl.CellData = HeaderInfoCellData(model)

    //コミットツリーセル
    inner class HeaderTreeCellData(private val commitList: CommitListCtrl,
                                   private val model: GviewHeadFilesModel) : CommitListCtrl.CellData() {

        override fun layout(tableCell: CommitListCtrl.Cell) {
            val canvas = Canvas(tableCell.width, tableCell.height)
            val ys = 0.0
            val ye = tableCell.height
            drawMark(canvas, model, ys, ye)
            tableCell.graphic = Pane(canvas)
            tableCell.text = null
        }

        private fun drawMark(canvas: Canvas, c: GviewHeadFilesModel, ys: Double, ye: Double) {
            val gc = setColor(canvas, laneNumber)
            val x = commitList.treeColumnWidth(laneNumber)
            val y = (ye - ys) / 2.0
            val xr = 7.0
            val yr = 7.0
            gc.fillOval(x - xr, y - yr, xr * 2.0, yr * 2.0)
        }
    }

    //コミット情報セル
    inner class HeaderInfoCellData(private val model: GviewHeadFilesModel) : CommitListCtrl.CellData() {
        override fun update(tableCell: CommitListCtrl.Cell) {
            val timeStamp = DateFormat.getDateTimeInstance().format(Date())
            val row1 = createTextLabel("ワークツリー情報 - ", timeStamp)
            val row2 = createTextLabel("ステージ済:", "${model.stagedFiles.size} ファイル")
            val row3 = createTextLabel("ステージ未:", "${model.changedFiles.size} ファイル")
            tableCell.graphic = VBox(row1, row2, row3)
            tableCell.text = null
            tableCell.style = "-fx-padding: 1 0 1 0;"
        }
    }

    //レーン番号
    val laneNumber: Int = head?.laneNumber ?: 0
}