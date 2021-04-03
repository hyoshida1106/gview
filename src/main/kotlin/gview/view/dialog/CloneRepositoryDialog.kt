package gview.view.dialog

import gview.view.framework.GvCustomDialog
import javafx.scene.control.ButtonType

class CloneRepositoryDialog(
        remotePath: String,
        localPath: String)
    : GvCustomDialog<CloneRepositoryDialogCtrl>(
        "取得するリポジトリのパス/URLと、作成するリポジトリのパスを指定してください",
        "/dialog/CloneRepositoryDialog.fxml",
        ButtonType.OK, ButtonType.CANCEL) {

    val remotePath: String get() = controller.remoteRepositoryPathProperty.value
    val localPath: String get() = controller.localDirectoryPathProperty.value
    val bareRepo: Boolean get() = controller.bareRepositoryProperty.value

    init {
        controller.setInitialPath(remotePath, localPath)
        addButtonHandler(ButtonType.OK, controller.btnOkDisable)
    }
}
