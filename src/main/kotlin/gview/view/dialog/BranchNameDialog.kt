package gview.view.dialog

import gview.view.framework.GvCustomDialog
import javafx.scene.control.ButtonType

class BranchNameDialog()
    : GvCustomDialog<BranchNameDialogCtrl>(
        "作成するブランチの名前を指定してください",
        "/dialog/BranchNameDialog.fxml",
        ButtonType.OK, ButtonType.CANCEL) {

    init {
        addButtonHandler(ButtonType.OK, controller.btnOkDisable)
    }
}
