package gview.view.dialog

import gview.resourceBundle
import gview.view.framework.GvCustomDialog
import javafx.scene.control.ButtonType
import org.jetbrains.annotations.NonNls

class CreateBranchByNameDialog() : GvCustomDialog<CreateBranchByNameDialogCtrl>(
    resourceBundle().getString("CreateBranchByName.Title"),
    "/dialog/CreateBranchByNameDialog.fxml",
    ButtonType.OK, ButtonType.CANCEL
) {
    init {
        addButtonHandler(ButtonType.OK, controller.btnOkDisable)
    }
}
