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

    //ツリーセル描画　共通メソッド

    //表示色
    val colors = arrayOf(
            "blue", "red", "teal", "slategrey", "green", "darkmagenta", "cadetblue",
            "darkolivegreen", "purple", "maroon")

    fun setColor(canvas: Canvas, lane: Int): GraphicsContext {
        val gc = canvas.graphicsContext2D
        val p = Paint.valueOf(colors[lane % colors.size])
        gc.lineWidth = 3.0
        gc.fill = p
        gc.stroke = p
        return gc
    }

    //情報セル描画　共通メソッド

    //ラベル付きテキスト表示
    fun createTextLabel(title:String, message:String): HBox {
        val titleLabel   = Label(title)
        titleLabel.style = CSS.titleStyle
        titleLabel.minWidth = Region.USE_PREF_SIZE;
        val messageLabel = Label(message)
        messageLabel.style = CSS.messageStyle
        HBox.setHgrow(titleLabel, Priority.NEVER)
        return HBox(titleLabel, messageLabel)
    }

    //CSS定義
    object CSS {
        //Style定義(タイトル)
        val titleStyle = """
                -fx-font-weight: bold;
                -fx-text-fill: #333333;
                -fx-padding: 1;
            """.trimIndent()
        //Style定義(メッセージ)
        val messageStyle = """
                -fx-padding: 1;
            """.trimIndent()
    }
}