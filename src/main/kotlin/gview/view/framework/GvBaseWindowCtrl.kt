package gview.view.framework

open class GvBaseWindowCtrl {

    // InnerClass - WindowObserver
    inner class WindowObserver {
        fun innerDisplayCompleted() { displayCompleted() }
        fun innerUpdateConfigInfo() { updateConfigInfo() }
    }

    // Class
    private val observer = WindowObserver()
    init {
        observers.add(observer)
    }
    fun finalize() {
        observers.remove(observer)
    }
    open fun displayCompleted() { }
    open fun updateConfigInfo() { }

    // Singleton
    companion object {
        val observers = mutableListOf<WindowObserver>()
        fun displayCompleted() { observers.forEach { it.innerDisplayCompleted() } }
        fun updateConfigInfo() { observers.forEach { it.innerUpdateConfigInfo() } }
    }
}
