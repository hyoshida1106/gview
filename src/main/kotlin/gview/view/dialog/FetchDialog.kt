package gview.view.dialog

import gview.resourceBundle
import gview.view.framework.GvCustomDialog
import javafx.scene.control.ButtonType
import org.eclipse.jgit.transport.RemoteConfig

class FetchDialog(remoteConfigList: List<RemoteConfig>) : GvCustomDialog<FetchDialogCtrl>(
    resourceBundle().getString("FetchDialog.Title"),
    FetchDialogCtrl(),
    ButtonType.OK, ButtonType.CANCEL
) {
    val remote get() = controller.remote
    val prune get() = controller.prune

    init {
        controller.remoteConfigList.value = remoteConfigList
    }
}