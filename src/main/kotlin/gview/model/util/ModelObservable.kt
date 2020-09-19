package gview.model.util

import javafx.application.Platform

open class ModelObservable<TargetClass> {

    private val observers = mutableListOf<(TargetClass) -> Unit>()

    fun addListener(callback: (TargetClass) -> Unit) {
        observers.add(callback)
    }

    fun fireCallback(target: TargetClass) {
        Platform.runLater { observers.forEach { it(target) } }
    }
}