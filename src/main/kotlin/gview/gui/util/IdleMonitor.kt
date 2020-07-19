package gview.gui.util

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Node
import javafx.scene.Scene
import javafx.util.Duration

// IDLE Timer
class IdleMonitor(idleTime: Int, notifier: () -> Unit ) {

    // 指定されたタイマ値を持つTimeLine
    private val idleTimeline: Timeline = Timeline(
        KeyFrame(Duration(idleTime.toDouble()), EventHandler { _ -> notifier() }))

    // イベントが通知された場合に notUIdle() を起動するEventHandler
    private val userEventHandler = EventHandler<Event> { notIdle() }

    // 指定された Scene にイベントフィルタを登録する
    fun register(scene: Scene, eventType: EventType<Event> = Event.ANY) {
        scene.addEventFilter(eventType, userEventHandler)
        start()
    }

    // 指定された Node にイベントフィルタを登録する
    fun register(node: Node, eventType: EventType<Event> = Event.ANY) {
        node.addEventFilter(eventType, userEventHandler)
        start()
    }

    // 指定された Scene のイベントフィルタを削除する
    fun unregister(scene: Scene, eventType: EventType<Event> = Event.ANY) {
        stop()
        scene.removeEventFilter(eventType, userEventHandler)
    }

    // 指定された Node のイベントフィルタを削除する
    fun unregister(node: Node, eventType: EventType<Event> = Event.ANY) {
        stop()
        node.removeEventFilter(eventType, userEventHandler)
    }

    // イベントを受信したならばタイマをリセットする
    private fun notIdle() {
        idleTimeline.playFromStart()
    }

    // タイマを起動する
    private fun start() {
        idleTimeline.cycleCount = 1;
        idleTimeline.playFromStart()
    }

    // タイマを停止する
    private fun stop() {
        idleTimeline.stop( ) 
    }
}
