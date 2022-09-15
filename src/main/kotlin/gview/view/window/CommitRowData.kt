package gview.view.window

import gview.view.menu.CommitRowContextMenu
import gview.view.util.GvCommitLabel
import gview.view.util.GvTextMessage
import gview.model.GvCommit
import gview.resourceBundle
import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Paint
import javafx.scene.shape.ArcType

class CommitRowData(commitList: CommitListCtrl, val model: GvCommit) : AbstractCommitRowData() {
    override val treeCellValue: CommitListCtrl.CellData = CommitTreeCellData(commitList, model)
    override val infoCellValue: CommitListCtrl.CellData = CommitInfoCellData(model)
    override val styleClassName: String = "commit-row"      // NON-NLS
    private val mergeMarkColor = "white"                    // NON-NLS

    //コミットツリーセル
    inner class CommitTreeCellData(private val commitList: CommitListCtrl, private val model: GvCommit) :
        CommitListCtrl.CellData {

        //コンテキストメニュー
        override val contextMenu = CommitRowContextMenu(model)

        override fun update(): Pair<Node?, String?> {
            return Pair(null, null)
        }

        //セル表示の更新
        override fun layout(tableCell: CommitListCtrl.Cell) {
            val canvas = Canvas(tableCell.width, tableCell.height)
            val ys = 0.0
            val ye = tableCell.height

            model.passThroughLanes.forEach { p -> drawPassThroughLine(canvas, p, ys, ye) }
            model.exitingLanes.forEach { b -> drawBranchLine(canvas, model.laneNumber, b, ys, ye) }
            model.enteringLanes.forEach { b -> drawMergeLine(canvas, model.laneNumber, b, ys, ye) }

            if (model.headerLane >= 0) {
                if (model.isHead) {
                    drawBranchLine(canvas, model.laneNumber, model.headerLane, ys, ye)
                } else {
                    drawPassThroughLine(canvas, model.headerLane, ys, ye)
                }
            }

            drawCommitMark(canvas, model, ys, ye)
            tableCell.graphic = Pane(canvas)
            tableCell.text = null
        }

        @Suppress("SameParameterValue")
        private fun drawCommitMark(canvas: Canvas, commit: GvCommit, ys: Double, ye: Double) {
            val lane = commit.laneNumber
            val gc = getGraphicContext(canvas, lane)
            val x = commitList.treeColumnWidth(lane)
            val y = (ye - ys) / 2.0
            val xr = markRadius
            val yr = markRadius
            gc.fillOval(x - xr, y - yr, xr * 2.0, yr * 2.0)
            if (model.isMerge) {
                gc.fill = Paint.valueOf(mergeMarkColor)
                gc.fillOval(x - xr / 2.0, y - yr / 2.0, xr, yr)
            }
        }

        @Suppress("SameParameterValue")
        private fun drawPassThroughLine(
            canvas: Canvas,
            lane: Int,
            ys: Double,
            ye: Double
        ) {

            val gc = getGraphicContext(canvas, lane)
            val x = commitList.treeColumnWidth(lane)
            gc.strokeLine(x, ys, x, ye)
        }

        @Suppress("SameParameterValue")
        private fun drawBranchLine(
            canvas: Canvas,
            currentLane: Int,
            branchLane: Int,
            ys: Double,
            ye: Double
        ) {

            val gc = getGraphicContext(canvas, branchLane)
            val xs = commitList.treeColumnWidth(currentLane)
            val xe = commitList.treeColumnWidth(branchLane)
            val ym = (ye - ys) / 2.0 - 2.0
            val xr = commitList.xPitch
            val yr = ym - ys
            when {
                xs < xe -> {
                    gc.strokeLine(xs, ym, xe - xr, ym)
                    gc.strokeLine(xe, ym - yr, xe, ys)
                    gc.strokeArc(
                        xe - 2.0 * xr, ym - 2.0 * yr, 2.0 * xr, 2.0 * yr,
                        270.0, 90.0, ArcType.OPEN
                    )
                }
                xs > xe -> {
                    gc.strokeLine(xs, ym, xe + xr, ym)
                    gc.strokeLine(xe, ym - yr, xe, ys)
                    gc.strokeArc(
                        xe, ym - 2.0 * yr, 2.0 * xr, 2.0 * yr,
                        180.0, 90.0, ArcType.OPEN
                    )
                }
                else -> {
                    gc.strokeLine(xs, ys, xs, ym)
                }
            }
        }

        @Suppress("SameParameterValue")
        private fun drawMergeLine(
            canvas: Canvas,
            currentLane: Int,
            mergeLane: Int,
            ys: Double,
            ye: Double
        ) {
            val gc = getGraphicContext(canvas, mergeLane)
            val xs = commitList.treeColumnWidth(mergeLane)
            val xe = commitList.treeColumnWidth(currentLane)
            val ym = (ye - ys) / 2.0 + 2.0
            val xr = commitList.xPitch
            val yr = ye - ym
            when {
                xs > xe -> {
                    gc.strokeLine(xs, ye, xs, ym + yr)
                    gc.strokeLine(xe, ym, xs - xr, ym)
                    gc.strokeArc(
                        xs - 2.0 * xr, ym, 2.0 * xr, 2.0 * yr,
                        0.0, 90.0, ArcType.OPEN
                    )
                }
                xs < xe -> {
                    gc.strokeLine(xs, ye, xs, ym + yr)
                    gc.strokeLine(xs + xr, ym, xe, ym)
                    gc.strokeArc(
                        xs, ym, 2.0 * xr, 2.0 * yr,
                        90.0, 90.0, ArcType.OPEN
                    )
                }
                else -> {
                    gc.strokeLine(xe, ye, xe, ym)
                }
            }
        }
    }

    //コミット情報セル
    inner class CommitInfoCellData(private val model: GvCommit) : CommitListCtrl.CellData {
        override val contextMenu = CommitRowContextMenu(model)

        override fun update(): Pair<Node?, String?> {
            val row1 = GvTextMessage(resourceBundle().getString("Title.Date"), model.commitTime)
            val row2 = GvTextMessage(resourceBundle().getString("Title.Author"), model.author)
            val row3 = GvTextMessage(resourceBundle().getString("Title.Comment"), model.shortMessage)
            row1.children.addAll(GvCommitLabel(model))
            return Pair(VBox(row1, row2, row3), null)
        }

        override fun layout(tableCell: CommitListCtrl.Cell) {
        }
    }
}