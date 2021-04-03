package gview.view.framework

/**
 * Window Controllerの基本クラス
 */
open class GvBaseWindowCtrl {

    /**
     *  イベント通知を受信するための内部クラス
     */
    inner class WindowObserver {
        /**
         * 表示完了時にControlクラスのメソッドを呼び出す
         */
        fun innerDisplayCompleted()   { displayCompleted() }

        /**
         * モーダル情報の更新時にコールする
         */
        fun innerUpdateConfigInfo()   { updateConfigInfo() }
    }

    /**
     * イベント受信クラスのインスタンス
     */
    private val observer = WindowObserver()

    init {
        // 初期化時にイベント受信クラスのインスタンスをリストに追加する
        observers.add(observer)
    }

    fun finalize() {
        // インスタンス解放時に(一応)リストから削除する
        observers.remove(observer)
    }

    // Observerからコールされる実処理メソッド

    /**
     * 表示完了通知
     */
    open fun displayCompleted() { }

    /**
     * モーダル情報更新
     */
    open fun updateConfigInfo() { }

    // クラスメソッド/変数
    companion object {
        /**
         * イベント受信クラス参照をリストとして保持する
         */
        val observers = mutableListOf<WindowObserver>()

        /**
         * すべてのWindowに表示完了を通知する
         */
        fun displayCompleted() { observers.forEach { it.innerDisplayCompleted() } }

        /**
         * すべてのWindowにモーダル情報更新を通知する
         */
        fun updateConfigInfo() { observers.forEach { it.innerUpdateConfigInfo() } }
    }
}
