package gview.view.dialog

import gview.resourceBundle
import gview.view.framework.GvCustomDialog
import javafx.scene.control.ButtonType

class MergeDialog: GvCustomDialog<MergeDialogCtrl>(
    resourceBundle().getString("Message.MergeComment"),
    "/dialog/MergeDialog.fxml",
    ButtonType.OK, ButtonType.CANCEL
) {
    val message: String get() = controller.message.trim()
}
