package gview.view.dialog

import gview.view.framework.GvCustomDialog
import gview.model.GvCommitFile
import javafx.scene.control.ButtonType

class SelectChangedFilesDialog(title: String): GvCustomDialog<SelectChangedFilesDialogCtrl>(
    title,
    SelectChangedFilesDialogCtrl(),
    ButtonType.OK, ButtonType.CANCEL
) {
    val selectedFiles: List<GvCommitFile> get() = controller.selectedFiles
}
