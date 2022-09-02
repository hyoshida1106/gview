package gview.view.main

import gview.view.framework.GvBaseWindow

/**
 * メインウィンドウオブジェクト
 */
object MainWindow: GvBaseWindow<MainWindowCtrl>("/view/MainWindow.fxml", MainWindowCtrl()) {      // NON-NLS
    fun runTask(function: () -> Unit) {
        controller.runTask(function)
    }
}