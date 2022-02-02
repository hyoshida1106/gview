package gview.view.util

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Node
import javafx.scene.Scene
import javafx.util.Duration

/**
 * TimeLineを利用したアイドルタイマ
 *
 * @param idleTime      アイドル時間を秒で指定する
 * @param handler       アイドル時処理
 */
class GvIdleTimer(idleTime: Int, handler: () -> Unit ) {

    /**
     *  指定されたタイマ値を持つTimeLine
     */
    private val idleTimeline: Timeline = Timeline(
        KeyFrame(Duration(idleTime.toDouble()), { _ -> handler() }))

    /**
     *  イベントが通知された場合に notUIdle() を起動するEventHandler
     */
    private val userEventHandler = EventHandler<Event> { resetTimer() }

    /**
     * 指定された Scene にイベントフィルタを登録する
     */
    fun register(scene: Scene, eventType: EventType<Event> = Event.ANY) {
        scene.addEventFilter(eventType, userEventHandler)
        startTimer()
    }

    /**
     * 指定された Node にイベントフィルタを登録する
     */
    fun register(node: Node, eventType: EventType<Event> = Event.ANY) {
        node.addEventFilter(eventType, userEventHandler)
        startTimer()
    }

    /**
     * 指定された Scene のイベントフィルタを削除する
     */
    fun unregister(scene: Scene, eventType: EventType<Event> = Event.ANY) {
        stopTimer()
        scene.removeEventFilter(eventType, userEventHandler)
    }

    /**
     * 指定された Node のイベントフィルタを削除する
     */
    fun unregister(node: Node, eventType: EventType<Event> = Event.ANY) {
        stopTimer()
        node.removeEventFilter(eventType, userEventHandler)
    }

    /**
     * イベントを受信したならばタイマをリセットする
     */
    private fun resetTimer() {
        idleTimeline.playFromStart()
    }

    /**
     * タイマを起動する
     */
    private fun startTimer() {
        idleTimeline.cycleCount = 1;
        idleTimeline.playFromStart()
    }

    /**
     * タイマを停止する
     */
    private fun stopTimer() {
        idleTimeline.stop( ) 
    }
}
