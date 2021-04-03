package gview.view.dialog

import gview.view.framework.GvCustomDialog
import gview.model.commit.GviewGitFileEntryModel
import javafx.scene.control.ButtonType

class SelectStageFilesDialog
    : GvCustomDialog<SelectStageFilesDialogCtrl>(
        "ステージするファイルを選択してください",
        "/dialog/SelectStageFilesDialog.fxml",
        ButtonType.OK, ButtonType.CANCEL) {
    val selectedFiles: List<GviewGitFileEntryModel> get() = controller.selectedFiles
}
