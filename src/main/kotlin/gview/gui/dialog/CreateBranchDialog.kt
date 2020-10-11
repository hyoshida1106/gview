package gview.gui.dialog

import gview.gui.framework.GviewCustomDialog
import javafx.scene.control.ButtonType

class CreateBranchDialog()
    : GviewCustomDialog<CreateBranchDialogCtrl>(
        "作成するブランチの名前と開始点を指定してください",
        "/dialog/CreateBranchDialog.fxml",
        ButtonType.OK, ButtonType.CANCEL) {

    init {
        addButtonHandler(ButtonType.OK, controller.btnOkDisable)
    }
}
