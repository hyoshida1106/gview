package gview.view.dialog

import gview.resourceBundle
import gview.view.framework.GvCustomDialog
import javafx.scene.control.ButtonType

class CreateBranchDialog : GvCustomDialog<CreateBranchDialogCtrl>(
    resourceBundle().getString("CreateBranchDialog.Title"),
    CreateBranchDialogCtrl(),
    ButtonType.OK, ButtonType.CANCEL
) {

    init {
        addButtonHandler(ButtonType.OK, controller.btnOkDisable)
    }
}
