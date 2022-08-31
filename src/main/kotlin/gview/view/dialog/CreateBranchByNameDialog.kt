package gview.view.dialog

import gview.resourceBundle
import gview.view.framework.GvCustomDialog
import javafx.scene.control.ButtonType

class CreateBranchByNameDialog() : GvCustomDialog<CreateBranchByNameDialogCtrl>(
    resourceBundle().getString("CreateBranchByNameDialog.Title"),
    "/dialog/CreateBranchByNameDialog.fxml",
    ButtonType.OK, ButtonType.CANCEL
) {
    init {
        addButtonHandler(ButtonType.OK, controller.btnOkDisable)
    }
}
