package gview.gui.dialog

import gview.gui.framework.GviewDialog
import gview.gui.main.MainWindow
import javafx.scene.control.Alert

class ErrorDialog(message: String?): Alert(AlertType.ERROR, message), GviewDialog<Unit> {

    constructor(e: Exception) : this(e.message)

    override fun showDialog() {
        initOwner(MainWindow.root.scene.window)
        title = "Error"
        headerText = null
        showAndWait()
    }
}