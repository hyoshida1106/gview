package gview.view.dialog

import gview.resourceBundle
import gview.view.framework.GvDialogInterface
import gview.view.window.MainWindow
import javafx.scene.control.Alert

class InformationDialog(message:String):
        Alert(AlertType.INFORMATION, message),
        GvDialogInterface<Unit> {

    override fun showDialog() {
        initOwner(MainWindow.root.scene.window)
        title = resourceBundle().getString("InformationDialog.Title")
        headerText = null
        showAndWait()
    }
}