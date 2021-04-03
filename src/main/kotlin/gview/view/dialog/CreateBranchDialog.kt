package gview.view.dialog

import gview.view.framework.GvCustomDialog
import javafx.scene.control.ButtonType

class CreateBranchDialog()
    : GvCustomDialog<CreateBranchDialogCtrl>(
        "作成するブランチの名前と開始点を指定してください",
        "/dialog/CreateBranchDialog.fxml",
        ButtonType.OK, ButtonType.CANCEL) {

    init {
        addButtonHandler(ButtonType.OK, controller.btnOkDisable)
    }
}
