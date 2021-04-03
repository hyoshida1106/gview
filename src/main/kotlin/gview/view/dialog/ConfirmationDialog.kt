package gview.view.dialog

import gview.view.framework.GvDialogInterface
import gview.view.main.MainWindow
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType

class ConfirmationDialog(
        private val type: ConfirmationType,
        private val message: String)
    : GvDialogInterface<Boolean> {

    enum class ConfirmationType { YesNo, OkCancel }

    override fun showDialog()
            : Boolean {

        val buttonTypes = when(type) {
            ConfirmationType.YesNo -> { arrayOf(
                ButtonType("はい", ButtonBar.ButtonData.OK_DONE),
                ButtonType("いいえ", ButtonBar.ButtonData.CANCEL_CLOSE)
            )}
            ConfirmationType.OkCancel -> { arrayOf(
                ButtonType("OK", ButtonBar.ButtonData.OK_DONE),
                ButtonType("キャンセル", ButtonBar.ButtonData.CANCEL_CLOSE)
            )}
        }
        val alert = Alert(Alert.AlertType.CONFIRMATION, message, *buttonTypes)
        alert.initOwner(MainWindow.root.scene.window)
        alert.title = "Confirmation"
        alert.headerText = null
        val result = alert.showAndWait()
        return result.isPresent && result.get().buttonData == ButtonBar.ButtonData.OK_DONE
    }
}