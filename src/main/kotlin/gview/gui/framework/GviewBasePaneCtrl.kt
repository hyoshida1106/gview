package gview.gui.framework

/*
    Controllerの基本クラス
 */
open class GviewBasePaneCtrl {

    /*
        継承クラスのインスタンスすべてにObserverインスタンスを所持させることにより、Observerリストを作成する
        そのリストを利用して、全コントロールインスタンスへの通知処理を実装する
        現時点で実装している通知は以下の2種類で、BaseControllerのクラスメソッドを呼び出して通知を行う
        ・displayCompleted           表示完了
        ・updateConfigInfo           設定情報の永続化前の情報更新
     */

    // イベント通知を受信するための内部クラス
    inner class Observer {
        fun innerDisplayCompleted()   { displayCompleted() }        //表示完了
        fun innerUpdateConfigInfo()   { updateConfigInfo() }        //モーダル情報の保存タイミング
    }
    private val observer = Observer()

    // インスタンス生成時にObserverをリストに登録する
    init {
        observers.add(observer)
    }

    // Observerからコールされる実処理メソッド
    open fun displayCompleted() { }                                 //表示完了
    open fun updateConfigInfo() { }                                 //モーダル情報の保存タイミング

    // クラスメソッド/変数
    companion object {
        //Observerのリスト
        val observers = mutableListOf<Observer>()
        //リストの全インスタンスにイベントを通知する
        fun displayCompleted() { observers.forEach { it.innerDisplayCompleted() } }
        fun updateConfigInfo() { observers.forEach { it.innerUpdateConfigInfo()  } }
    }
}
