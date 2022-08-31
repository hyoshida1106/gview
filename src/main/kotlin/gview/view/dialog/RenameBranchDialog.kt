package gview.view.dialog

import gview.resourceBundle
import gview.view.framework.GvCustomDialog
import javafx.scene.control.ButtonType

class RenameBranchDialog(private val lastBranchName: String) : GvCustomDialog<RenameBranchDialogCtrl>(
    resourceBundle().getString("RenameBranchDialog.Title"),
    "/dialog/RenameBranchDialog.fxml",
    ButtonType.OK, ButtonType.CANCEL
) {
    init {
        addButtonHandler(ButtonType.OK, controller.btnOkDisable)
    }

    override fun showDialog(): ButtonType? {
        controller.newBranchName = lastBranchName
        return super.showDialog()
    }
}