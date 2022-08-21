package gview.view.dialog

import gview.view.framework.GvDialogInterface
import gview.view.main.MainWindow
import javafx.scene.control.*

class RemoveRemoteBranchDialog(private val message: String) : GvDialogInterface<Boolean> {
    override fun showDialog(): Boolean {
        val alert = Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL)
        alert.initOwner(MainWindow.root.scene.window)
        alert.headerText = null
        val result = alert.showAndWait()
        return result.isPresent && result.get().buttonData == ButtonBar.ButtonData.OK_DONE
    }
}