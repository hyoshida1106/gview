package gview.view.util

import javafx.animation.Animation.INDEFINITE
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.Event
import javafx.stage.Stage
import javafx.util.Duration

/**
 * TimeLineを利用したアイドルタイマ
 *
 * @param stage         登録するStage
 * @param idleTime      アイドル時間をミリ秒で指定する
 * @param repeat        繰り返し実施指示
 * @param handler       アイドル時処理
 */
class GvIdleTimer(private val stage: Stage, idleTime: Int, private val repeat: Boolean, val handler: () -> Unit ) {

    /**
     *  指定されたタイマ値を持つTimeLine
     */
    private val idleTimeline: Timeline = Timeline(
        KeyFrame(Duration(idleTime.toDouble()), { handler() })
    )

    /**
     * 初期化
     */
    init {
        idleTimeline.cycleCount = if (repeat) INDEFINITE else 1
        stage.scene.addEventFilter(Event.ANY) { resetTimer() }
        stage.focusedProperty().addListener { _, _, _ -> resetTimer() }
        resetTimer()
    }

    /**
     * タイマのセット/リセット。
     * フォーカスの有無によって処理を変更する。
     */
    private fun resetTimer() {
        if (stage.isFocused) {
            idleTimeline.playFromStart()
        } else {
            idleTimeline.stop()
        }
    }
}
