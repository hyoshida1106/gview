package gview.gui.dialog

import gview.gui.framework.GviewCustomDialog
import gview.model.commit.GviewGitFileEntryModel
import javafx.scene.control.ButtonType

class SelectUnStageFilesDialog: GviewCustomDialog<SelectUnStageFilesDialogCtrl>(
        "アンステージするファイルを選択してください",
        "/dialog/SelectUnStageFilesDialog.fxml",
        ButtonType.OK, ButtonType.CANCEL) {
    val selectedFiles: List<GviewGitFileEntryModel> get() = controller.selectedFiles
}
