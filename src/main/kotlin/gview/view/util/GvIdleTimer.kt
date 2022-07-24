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
 * @param idleTime      アイドル時間を秒で指定する
 * @param handler       アイドル時処理
 * @param repeat        繰り返し実施指示
 */
class GvIdleTimer(private val stage: Stage, idleTime: Int, private val repeat: Boolean, val handler: () -> Unit ) {

    /**
     *  指定されたタイマ値を持つTimeLine
     */
    private val idleTimeline: Timeline = Timeline(
        KeyFrame(Duration(idleTime.toDouble()), { handler() })
    )

    init {
        idleTimeline.cycleCount = if (repeat) INDEFINITE else 1
        stage.scene.addEventFilter(Event.ANY) { resetTimer() }
        stage.focusedProperty().addListener { _, _, _ -> resetTimer() }
        resetTimer()
    }

    private fun resetTimer() {
        if (stage.isFocused) {
            idleTimeline.playFromStart()
        } else {
            idleTimeline.stop()
        }
    }
}
