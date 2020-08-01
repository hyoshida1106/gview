package gview.gui.commitlist

import gview.model.commit.GviewCommitDataModel
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Paint
import javafx.scene.shape.ArcType

class CommitRow(commitList: CommitListCtrl, model: GviewCommitDataModel): BaseRow() {

    override val treeCellValue: CommitListCtrl.CellData = CommitTreeCellData(commitList, model)
    override val infoCellValue: CommitListCtrl.CellData = CommitInfoCellData(model)

    //コミットツリーセル
    inner class CommitTreeCellData(private val commitList: CommitListCtrl,
                                   private val model: GviewCommitDataModel): CommitListCtrl.CellData() {

        //表示更新
        override fun layout(tableCell: CommitListCtrl.Cell) {
            val canvas = Canvas(tableCell.width, tableCell.height)
            val ys = 0.0
            val ye = tableCell.height
            model.passWays.forEach { p -> drawPassingWay(canvas, p, ys, ye)}
            model.branchTo.forEach { b -> drawBranchLine(canvas, model.laneNumber, b, ys, ye) }
            model.mergeFrom.forEach { b -> drawMergeLine(canvas, model.laneNumber, b, ys, ye) }
            drawLane(canvas, model, ys, ye)
            tableCell.graphic = Pane(canvas)
            tableCell.text = null
        }

        private fun drawLane(canvas: Canvas, c: GviewCommitDataModel, ys: Double, ye: Double) {
            val lane = c.laneNumber
            val gc = setColor(canvas, lane)
            val x = commitList.treeColumnWidth(lane)
            val y = (ye - ys) / 2.0
            val xr = 7.0
            val yr = 7.0
            gc.fillOval(x - xr, y - yr, xr * 2.0, yr * 2.0)
            if(model.isMerge) {
                gc.fill = Paint.valueOf("white")
                gc.fillOval(x - xr / 2.0, y - yr / 2.0, xr, yr)
            }
        }

        private fun drawPassingWay(canvas: Canvas, lane: Int, ys: Double, ye: Double) {
            val gc = setColor(canvas, lane)
            val x = commitList.treeColumnWidth(lane)
            gc.strokeLine(x, ys, x, ye)
        }

        private fun drawBranchLine(canvas: Canvas, currentLane: Int, branchLane: Int, ys: Double, ye: Double) {
            val gc = setColor(canvas, branchLane)
            val xs = commitList.treeColumnWidth(currentLane)
            val xe = commitList.treeColumnWidth(branchLane)
            val ym = (ye - ys) / 2.0 - 2.0
            val xr = commitList.xPitch
            val yr = xr
            when {
                xs < xe -> {
                    gc.strokeLine(xs, ym, xe - xr, ym)
                    gc.strokeLine(xe, ym - yr, xe, ys)
                    gc.strokeArc(xe - 2.0 * xr, ym - 2.0 * yr, 2.0 * xr, 2.0 * yr,
                            270.0, 90.0, ArcType.OPEN)
                }
                xs > xe -> {
                    gc.strokeLine(xs, ym, xe + xr, ym)
                    gc.strokeLine(xe, ym - yr, xe, ys)
                    gc.strokeArc(xe, ym - 2.0 * yr, 2.0 * xr, 2.0 * yr,
                            180.0, 90.0, ArcType.OPEN)
                }
                else -> {
                    gc.strokeLine(xs, ys, xs, ym)
                }
            }
        }

        private fun drawMergeLine(canvas: Canvas, currentLane: Int, mergeLane: Int, ys: Double, ye: Double) {
            val gc = setColor(canvas, mergeLane)
            val xs = commitList.treeColumnWidth(mergeLane)
            val xe = commitList.treeColumnWidth(currentLane)
            val ym = (ye - ys) / 2.0 + 2.0
            val xr = commitList.xPitch
            val yr = ye - ym
            when {
                xs > xe -> {
                    gc.strokeLine(xs, ye, xs, ym + yr)
                    gc.strokeLine(xe, ym, xs - xr, ym)
                    gc.strokeArc(xs - 2.0 * xr, ym, 2.0 * xr, 2.0 * yr,
                            0.0, 90.0, ArcType.OPEN)
                }
                xs < xe -> {
                    gc.strokeLine(xs, ye, xs, ym + yr)
                    gc.strokeLine(xs + xr, ym, xe, ym)
                    gc.strokeArc(xs, ym, 2.0 * xr, 2.0 * yr,
                            90.0, 90.0, ArcType.OPEN)
                }
                else -> {
                    gc.strokeLine(xe, ye, xe, ym)
                }
            }
        }

    }

    //コミット情報セル
    inner class CommitInfoCellData(private val model: GviewCommitDataModel): CommitListCtrl.CellData() {

        override fun update(tableCell: CommitListCtrl.Cell) {
            //日付・作者・メッセージ抜粋
            val row1 = createTextLabel("日付: ", model.commitTime)
            val row2 = createTextLabel("作者: ", model.committer)
            val row3 = createTextLabel("コメント:", model.shortMessage)

            //ローカルブランチ
            model.localBranches.forEach {
                val label = Label(it.name)
                label.style = CSS.localBranchStyle
                row1.children.add(label)
            }
            model.remoteBranches.forEach {
                val label = Label("remote/${it.name}")
                label.style = CSS.remoteBranchStyle
                row1.children.add(label)
            }

            model.tags.forEach {
                val label = Label(it)
                label.style = CSS.tagStyle
                row1.children.add(label)
            }

            tableCell.graphic = VBox(row1, row2, row3)
            tableCell.text = null

            tableCell.style = "-fx-padding: 1 0 1 0;"
        }
    }

    object CSS {
        private val labelStyle = """
            -fx-font-size: 0.8em;
            -fx-font-weight: bold;
            -fx-padding: 0 5 0 5;
            -fx-background-insets: 0 2 0 0;
            -fx-border-style: solid;
            -fx-border-color: rgb(80,80,80);
            -fx-border-width: 2;
            -fx-border-radius: 2;
            -fx-border-insets: 0 2 0 0;  
        """.trimIndent()
        //ローカルブランチ
        val localBranchStyle = labelStyle + """
            -fx-background-color: rgb(117, 207, 77);
        """.trimIndent()
        //リモートブランチ
        val remoteBranchStyle = labelStyle + """
            -fx-background-color: rgb(206, 141, 128);
        """.trimIndent()
        //タグ
        val tagStyle = labelStyle + """
            -fx-background-color: rgb(238, 238, 148);
        """.trimIndent()
    }
}