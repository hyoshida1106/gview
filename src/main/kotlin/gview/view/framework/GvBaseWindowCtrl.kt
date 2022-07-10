package gview.view.framework

/**
 * 基本ウィンドウコントロールクラス
 */
open class GvBaseWindowCtrl {

    /**
     * オブザーバとして登録する内部クラス
     */
    inner class WindowObserver {
        /**
         * 表示完了時に呼び出されるメソッド
         */
        fun innerDisplayCompleted() {
            displayCompleted()
        }

        /**
         * 設定情報変更時に呼び出されるメソッド
         */
        fun innerUpdateConfigInfo() {
            updateConfigInfo()
        }
    }

    /**
     * ウィンドウインスタンスが保持するオブザーバインスタンス
     */
    private val observer = WindowObserver()

    /**
     * 初期化
     */
    init {
        //自身のオブザーバ登録を行う
        observers.add(observer)
    }

    /**
     * 終了処理
     */
    fun finalize() {
        //念のため
        observers.remove(observer)
    }

    /**
     * 表示完了時処理が必要な場合のフック
     */
    open fun displayCompleted() {}

    /**
     * 設定更新が必要な場合のフック
     */
    open fun updateConfigInfo() {}

    /**
     * Observer/Observable実現のためのシングルトンオブジェクト
     */
    companion object {
        /**
         * Observerリスト
         */
        val observers = mutableListOf<WindowObserver>()

        /**
         * 表示完了時の呼び出し処理
         */
        fun displayCompleted() {
            observers.forEach { it.innerDisplayCompleted() }
        }

        /**
         * 設定更新時の呼び出し処理
         */
        fun updateConfigInfo() {
            observers.forEach { it.innerUpdateConfigInfo() }
        }
    }
}
