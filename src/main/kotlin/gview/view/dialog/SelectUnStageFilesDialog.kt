package gview.view.dialog

import gview.view.framework.GvCustomDialog
import gview.model.commit.GvCommitFile
import javafx.scene.control.ButtonType

class SelectUnStageFilesDialog
    : GvCustomDialog<SelectUnStageFilesDialogCtrl>(
        "アンステージするファイルを選択してください",
        "/dialog/SelectUnStageFilesDialog.fxml",
        ButtonType.OK, ButtonType.CANCEL) {
    val selectedFiles: List<GvCommitFile> get() = controller.selectedFiles
}
