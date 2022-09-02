package gview.view.dialog

import gview.view.framework.GvCustomDialog
import gview.model.commit.GvCommitFile
import javafx.scene.control.ButtonType

class SelectChangedFilesDialog(title: String): GvCustomDialog<SelectChangedFilesDialogCtrl>(
    title,
    "/dialog/SelectChangedFilesDialog.fxml",    // NON-NLS
    SelectChangedFilesDialogCtrl(),
    ButtonType.OK, ButtonType.CANCEL
) {
    val selectedFiles: List<GvCommitFile> get() = controller.selectedFiles
}
