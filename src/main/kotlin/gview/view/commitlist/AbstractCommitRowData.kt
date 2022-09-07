package gview.view.commitlist

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Paint

/*
    コミットリスト行データの共通ベースクラス
 */
abstract class AbstractCommitRowData: CommitListCtrl.RowData {

    protected val markRadius = 4.5
    private   val lineWidth  = 2.5

    /**
     * レーン番号に対する表示色を定義する
     */
    private val colors = arrayOf(
        "blue", "red", "teal", "slateGrey", "green", "darkMagenta", "cadetBlue",    // NON-NLS
        "darkOliveGreen", "purple", "maroon"    )                                   // NON-NLS

    /**
     * 樹形図描画用のGraphic Contextを取得する
     *
     * @param[canvas]   描画Canvasインスタンス
     * @param[lane]     レーン番号
     * @return          Graphic Context
     */
    fun getGraphicContext(canvas: Canvas, lane: Int): GraphicsContext {
        val gc = canvas.graphicsContext2D
        val p = Paint.valueOf(colors[lane % colors.size])
        gc.lineWidth = lineWidth
        gc.fill = p
        gc.stroke = p
        return gc
    }
}