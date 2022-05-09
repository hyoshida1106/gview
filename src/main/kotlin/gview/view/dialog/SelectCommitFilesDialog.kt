package gview.view.dialog

import gview.view.framework.GvCustomDialog
import gview.model.commit.GviewGitFileEntryModel
import javafx.scene.control.ButtonType

class SelectCommitFilesDialog
    : GvCustomDialog<SelectCommitFilesDialogCtrl>(
        "コミットするファイルを選択し、メッセージを入力してください",
        "/dialog/SelectCommitFilesDialog.fxml",
        ButtonType.OK, ButtonType.CANCEL) {

    val selectedFiles: List<GviewGitFileEntryModel> get() = controller.selectedFiles
    val message:String get() = controller.message
}