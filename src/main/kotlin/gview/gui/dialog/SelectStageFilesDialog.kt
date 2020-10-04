package gview.gui.dialog

import gview.gui.framework.GviewCustomDialog
import gview.model.commit.GviewGitFileEntryModel
import javafx.scene.control.ButtonType

class SelectStageFilesDialog
    : GviewCustomDialog<SelectStageFilesDialogCtrl>(
        "ステージするファイルを選択してください",
        "/dialog/SelectStageFilesDialog.fxml",
        ButtonType.OK, ButtonType.CANCEL) {
    val selectedFiles: List<GviewGitFileEntryModel> get() = controller.selectedFiles
}
