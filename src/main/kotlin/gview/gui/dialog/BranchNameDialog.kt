package gview.gui.dialog

import gview.gui.framework.GviewCustomDialog
import javafx.scene.control.ButtonType

class BranchNameDialog()
    : GviewCustomDialog<BranchNameDialogCtrl>(
        "作成するブランチの名前を指定してください",
        "/dialog/BranchNameDialog.fxml",
        ButtonType.OK, ButtonType.CANCEL) {

    init {
        addButtonHandler(ButtonType.OK, controller.btnOkDisable)
    }
}
