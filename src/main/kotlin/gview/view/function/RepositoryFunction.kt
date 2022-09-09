package gview.view.function

import gview.conf.SystemModal
import gview.model.GvRepository
import gview.resourceBundle
import gview.view.dialog.CloneRepositoryDialog
import gview.view.dialog.ErrorDialog
import gview.view.dialog.FetchDialog
import gview.view.window.MainWindow
import javafx.scene.control.ButtonType
import javafx.stage.DirectoryChooser

object RepositoryFunction {

    fun canFetch(repository: GvRepository?): Boolean {
        return repository?.remoteConfigList?.isNotEmpty() ?: false
    }

    fun doFetch(repository: GvRepository?) {
        if (repository == null) return
        val dialog = FetchDialog(repository.remoteConfigList)
        if (dialog.showDialog() == ButtonType.OK) {
            try {
                MainWindow.runTask { monitor ->
                    repository.fetch(monitor, dialog.remote, dialog.prune)
                }
            } catch (e: Exception) {
                ErrorDialog(e).showDialog()
            }
        }
    }

    fun doOpen(filePath: String) {
        try {
            MainWindow.runTask { ->
                GvRepository.open(filePath)
                SystemModal.addLastOpenedFile(filePath)
            }
        } catch (e: Exception) {
            ErrorDialog(e).showDialog()
        }
    }

    fun doOpen() {
        val chooser = DirectoryChooser()
        chooser.title = resourceBundle().getString("Message.OpenRepositoryPathMessage")
        val dir = chooser.showDialog(MainWindow.root.scene.window) ?: return
        doOpen(dir.absolutePath)
    }

    fun doCreate() {
        val chooser = DirectoryChooser()
        chooser.title = resourceBundle().getString("Message.CreateNewRepositoryPathMessage")
        val dir = chooser.showDialog(MainWindow.root.scene.window) ?: return
        try {
            MainWindow.runTask { ->
                GvRepository.init(dir.absolutePath)
                SystemModal.addLastOpenedFile(dir.absolutePath)
            }
        } catch (e: Exception) {
            ErrorDialog(e).showDialog()
        }
    }

    fun doClone() {
        val dialog = CloneRepositoryDialog("", "")
        if (dialog.showDialog() != ButtonType.OK) return
        try {
            MainWindow.runTask { monitor ->
                GvRepository.clone(monitor, dialog.localPath, dialog.remotePath, dialog.bareRepo)
                SystemModal.addLastOpenedFile(dialog.localPath)
            }
        } catch (e: Exception) {
            ErrorDialog(e).showDialog()
        }
    }

}