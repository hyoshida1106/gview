package gview.view.main

import gview.view.framework.GvBaseWindow

/**
 * メインウィンドウオブジェクト
 */
object MainWindow: GvBaseWindow<MainWindowCtrl>("/view/MainView.fxml", "MainWindow") {
    fun runTask(function: () -> Unit) {
        controller.runTask(function)
    }
}