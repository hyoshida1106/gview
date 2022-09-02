package gview.view.dialog

import gview.view.framework.GvCustomDialog
import gview.model.commit.GvCommitFile
import gview.resourceBundle
import javafx.scene.control.ButtonType

class SelectUnStageFilesDialog : GvCustomDialog<SelectUnStageFilesDialogCtrl>(
    resourceBundle().getString("SelectUnStageFilesDialog.Title"),
    "/dialog/SelectUnStageFilesDialog.fxml",            // NON-NLS
    SelectUnStageFilesDialogCtrl(),
    ButtonType.OK, ButtonType.CANCEL
) {
    val selectedFiles: List<GvCommitFile> get() = controller.selectedFiles
}
