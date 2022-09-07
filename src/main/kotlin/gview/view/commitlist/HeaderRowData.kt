package gview.view.commitlist

import gview.view.util.GvTextMessage
import gview.model.workfile.GvWorkFileList
import gview.resourceBundle
import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.control.ContextMenu
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import org.jetbrains.annotations.NonNls
import java.text.DateFormat
import java.util.*

class HeaderRowData(
    commitList: CommitListCtrl,
    val model: GvWorkFileList,
    val laneNumber: Int?
) : AbstractCommitRowData() {

    override val treeCellValue: CommitListCtrl.CellData = HeaderTreeCellData(commitList, model)
    override val infoCellValue: CommitListCtrl.CellData = HeaderInfoCellData(model)

    @NonNls
    override val styleClassName: String = "header-row"

    //コミットツリーセル
    inner class HeaderTreeCellData(private val commitList: CommitListCtrl, val model: GvWorkFileList) :
        CommitListCtrl.CellData {

        override fun update(): Pair<Node?, String?> {
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

        @Suppress("SameParameterValue")
        private fun drawMark(canvas: Canvas, ys: Double, ye: Double) {
            val gc = getGraphicContext(canvas, laneNumber ?: 0)
            val x = commitList.treeColumnWidth(laneNumber ?: 0)
            val y = (ye - ys) / 2.0
            val xr = markRadius
            val yr = markRadius
            gc.fillRect(x - xr, y - yr, xr * 2.0, yr * 2.0)
            if (laneNumber != null) gc.strokeLine(x, y, x, ye)
        }
    }

    //コミット情報セル
    inner class HeaderInfoCellData(private val model: GvWorkFileList) : CommitListCtrl.CellData {
        override fun update(): Pair<Node?, String?> {
            val timeStamp = DateFormat.getDateTimeInstance().format(Date())
            val row1 = GvTextMessage(resourceBundle().getString("Title.HeaderRow"), timeStamp)
            val row2 = GvTextMessage(resourceBundle().getString("Title.StagedFileNumber"), "${model.stagedFiles.value?.size ?: 0}")
            val row3 = GvTextMessage(resourceBundle().getString("Title.UpdateFileNumber"), "${model.changedFiles.value?.size ?: 0}")
            return Pair(VBox(row1, row2, row3), null)
        }

        override val contextMenu: ContextMenu? = null
    }

}