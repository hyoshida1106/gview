package gview.view.window

import gview.model.GvProgressMonitor
import gview.view.framework.GvBaseWindow
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.StageStyle

class ProgressWindow(monitor: GvProgressMonitor):
    GvBaseWindow<ProgressWindowCtrl>("/window/ProgressWindow.fxml", ProgressWindowCtrl(monitor)) {      // NON-NLS

    private val stage = Stage()

    init {
        stage.title = "GView"
        stage.scene = Scene(root)
        stage.initStyle(StageStyle.UTILITY)
        stage.onCloseRequest = EventHandler { it.consume() }        // "X"で閉じないようにする
    }

    fun show() {
        stage.show()
    }

    fun hide() {
        stage.hide()
    }
}