package gview.view.window

import gview.model.GvProgressMonitor
import gview.view.framework.GvBaseWindow

/**
 * メインウィンドウオブジェクト
 */
object MainWindow: GvBaseWindow<MainWindowCtrl>(MainWindowCtrl()) {
    fun runTask(function: () -> Unit) {
        controller.runTask(function)
    }
    fun runTask( function: (GvProgressMonitor) -> Unit) {
        controller.runTask(function)
    }
}