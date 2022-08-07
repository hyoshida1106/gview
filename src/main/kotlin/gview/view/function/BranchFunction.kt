package gview.view.function

import gview.model.GvRepository
import gview.view.dialog.ErrorDialog
import gview.view.dialog.FetchDialog
import javafx.scene.control.ButtonType
import org.eclipse.jgit.transport.RemoteConfig

object BranchFunction {

    val canFetch: Boolean get() {
        val repository = GvRepository.currentRepository ?: return false
        return RemoteConfig.getAllRemoteConfigs(repository.config).isNotEmpty()
    }

    fun doFetch( ) {
        val repository = GvRepository.currentRepository ?: return
        val dialog = FetchDialog(RemoteConfig.getAllRemoteConfigs(repository.config))
        if (dialog.showDialog() == ButtonType.OK) {
            try {
                repository.gitCommand.fetch()
                    .setRemote(dialog.remote)
                    .setRemoveDeletedRefs(dialog.prune)
                    .call()
                repository.branchChanged()
            } catch (e: Exception) {
                ErrorDialog(e).showDialog()
            }
        }
    }
}