package gview.view.framework

/**
 * ダイアログクラスの基盤になるインターフェース
 */
interface GvDialogInterface<T> {
    /**
     * ダイアログを表示する
     */
    fun showDialog(): T
}