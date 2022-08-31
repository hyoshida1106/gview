package gview.view.dialog

import gview.resourceBundle
import gview.view.framework.GvDialogInterface
import gview.view.main.MainWindow
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType

class ConfirmationDialog(private val type: ConfirmationType, private val message: String) : GvDialogInterface<Boolean> {

    enum class ConfirmationType { YesNo, OkCancel }

    override fun showDialog(): Boolean {
        val buttonTypes = when (type) {
            ConfirmationType.YesNo -> arrayOf(ButtonType.YES, ButtonType.NO)
            ConfirmationType.OkCancel -> arrayOf(ButtonType.OK, ButtonType.CANCEL)
        }
        val alert = Alert(Alert.AlertType.CONFIRMATION, message, *buttonTypes)
        alert.initOwner(MainWindow.root.scene.window)
        alert.title = resourceBundle().getString("ConfirmationDialog.title")
        alert.headerText = null
        val result = alert.showAndWait()
        return if(result.isPresent) result.get().buttonData == buttonTypes[0].buttonData else false
    }
}