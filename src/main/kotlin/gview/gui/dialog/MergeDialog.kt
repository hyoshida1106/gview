package gview.gui.dialog

import gview.gui.framework.GviewCustomDialog
import javafx.scene.control.ButtonType

class MergeDialog
    : GviewCustomDialog<MergeDialogCtrl>(
        "メッセージを入力してください",
        "/dialog/MergeDialog.fxml",
        ButtonType.OK, ButtonType.CANCEL) {
}
