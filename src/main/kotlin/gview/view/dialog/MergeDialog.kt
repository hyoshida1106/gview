package gview.view.dialog

import gview.view.framework.GvCustomDialog
import javafx.scene.control.ButtonType

class MergeDialog
    : GvCustomDialog<MergeDialogCtrl>(
        "メッセージを入力してください",
        "/dialog/MergeDialog.fxml",
        ButtonType.OK, ButtonType.CANCEL) {
}
