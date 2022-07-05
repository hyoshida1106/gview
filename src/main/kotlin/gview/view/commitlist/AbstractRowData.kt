package gview.view.commitlist

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Paint

/*
    コミットリスト行データの共通ベースクラス
 */
abstract class AbstractRowData: CommitListCtrl.RowData {

    protected val markRadius = 5.0
    protected val lineWidth  = 3.0

    //表示色
    private val colors = arrayOf(
            "blue", "red", "teal", "slateGrey", "green", "darkMagenta", "cadetBlue",
            "darkOliveGreen", "purple", "maroon")

    fun setColor(canvas: Canvas, lane: Int): GraphicsContext {
        val gc = canvas.graphicsContext2D
        val p = Paint.valueOf(colors[lane % colors.size])
        gc.lineWidth = lineWidth
        gc.fill = p
        gc.stroke = p
        return gc
    }
}