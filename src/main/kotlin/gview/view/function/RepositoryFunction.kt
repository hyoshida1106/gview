package gview.view.function

import gview.model.GvRepository
import gview.view.dialog.ErrorDialog
import gview.view.dialog.FetchDialog
import gview.view.main.MainWindow
import javafx.scene.control.ButtonType

object RepositoryFunction {

    fun canFetch(repository: GvRepository?): Boolean {
        return repository?.remoteConfigList?.isNotEmpty() ?: false
    }

    fun doFetch(repository: GvRepository?) {
        if(repository == null) return
        val dialog = FetchDialog(repository.remoteConfigList)
        if (dialog.showDialog() == ButtonType.OK) {
            try {
                MainWindow.runTask { repository.fetch(dialog.remote, dialog.prune) }
            } catch (e: Exception) {
                ErrorDialog(e).showDialog()
            }
        }
    }

}