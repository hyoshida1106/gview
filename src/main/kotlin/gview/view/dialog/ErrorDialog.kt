package gview.view.dialog

import gview.resourceBundle
import gview.view.framework.GvDialogInterface
import gview.view.main.MainWindow
import javafx.scene.control.Alert

class ErrorDialog(message: String?) : Alert(AlertType.ERROR, message), GvDialogInterface<Unit> {

    constructor(e: Exception) : this(e.localizedMessage) {
        e.printStackTrace()
    }

    override fun showDialog() {
        initOwner(MainWindow.root.scene.window)
        title = resourceBundle().getString("ErrorDialog.Title")
        headerText = null
        showAndWait()
    }
}