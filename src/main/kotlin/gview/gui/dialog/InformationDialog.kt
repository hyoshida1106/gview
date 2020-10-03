package gview.gui.dialog

import gview.gui.framework.GviewDialog
import gview.gui.main.MainWindow
import javafx.scene.control.Alert

class InformationDialog(message:String): Alert(AlertType.INFORMATION, message), GviewDialog<Unit> {

    override fun showDialog() {
        initOwner(MainWindow.root.scene.window)
        title = "Information"
        headerText = null
        showAndWait()
    }
}