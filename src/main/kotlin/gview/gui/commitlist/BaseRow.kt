package gview.gui.commitlist

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.paint.Paint

/*
    コミットリスト行データの共通ベースクラス
 */
abstract class BaseRow: CommitListCtrl.RowData {

    //表示色
    private val colors = arrayOf(
            "blue", "red", "teal", "slateGrey", "green", "darkMagenta", "cadetBlue",
            "darkOliveGreen", "purple", "maroon")

    fun setColor(canvas: Canvas, lane: Int): GraphicsContext {
        val gc = canvas.graphicsContext2D
        val p = Paint.valueOf(colors[lane % colors.size])
        gc.lineWidth = 3.0
        gc.fill = p
        gc.stroke = p
        return gc
    }

    //ラベル付きテキスト表示
    fun createTextLabel(title:String, message:String): HBox {
        val titleLabel   = Label(title)
        titleLabel.styleClass.add("title-string")
        titleLabel.minWidth = Region.USE_PREF_SIZE
        val messageLabel = Label(message)
        messageLabel.styleClass.add("message-string")
        HBox.setHgrow(titleLabel, Priority.NEVER)
        return HBox(titleLabel, messageLabel)
    }
}