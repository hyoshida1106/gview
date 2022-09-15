package gview.view.dialog

import gview.view.framework.GvCustomDialog
import gview.model.GvCommitFile
import gview.resourceBundle
import javafx.scene.control.ButtonType

class SelectCommitFilesDialog : GvCustomDialog<SelectCommitFilesDialogCtrl>(
    resourceBundle().getString("SelectCommitFilesDialog.Title"),
    SelectCommitFilesDialogCtrl(),
    ButtonType.OK, ButtonType.CANCEL
) {
    val selectedFiles: List<GvCommitFile> get() = controller.selectedFiles
    val message: String get() = controller.message
}
