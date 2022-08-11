package gview.view.function

import gview.model.branch.GvLocalBranch
import gview.resourceBundle
import gview.view.dialog.ErrorDialog
import gview.view.dialog.RemoveLocalBranchDialog
import gview.view.main.MainWindow
import org.eclipse.jgit.api.errors.NotMergedException

object BranchFunction {

    fun canCheckout(branch: GvLocalBranch?): Boolean {
        return branch?.isCurrentBranch?.not() ?: false
    }

    fun doCheckout(branch: GvLocalBranch) {
        try {
            MainWindow.runTask { branch.checkout() }
        } catch (e: Exception) {
            ErrorDialog(e).showDialog()
        }
    }

    fun canPull(branch: GvLocalBranch?): Boolean {
        return branch?.remoteBranch?.get() != null
    }

    fun doPull(branch: GvLocalBranch) {
        try {
            MainWindow.runTask { branch.pull() }
        } catch (e: Exception) {
            ErrorDialog(e).showDialog()
        }
    }


    fun canPush(branch: GvLocalBranch?): Boolean {
        return branch?.hasRemoteConf ?: false
    }

    fun doPush(branch: GvLocalBranch) {
        try {
            MainWindow.runTask { branch.push() }
        } catch (e: Exception) {
            ErrorDialog(e).showDialog()
        }
    }


    fun canRemove(branch: GvLocalBranch?): Boolean {
        return branch?.isCurrentBranch?.not() ?: false
    }

    fun doRemove(branch: GvLocalBranch) {
        val dialog = RemoveLocalBranchDialog(
            String.format(resourceBundle().getString("Message.ConfirmToRemove"), branch.name))
        if (dialog.showDialog()) {
            try {
                MainWindow.runTask { branch.remove(dialog.forceRemove) }
            } catch (e:NotMergedException) {
                ErrorDialog(resourceBundle().getString("Message.BranchNotMerged").format(branch.name)).showDialog()
            } catch (e: Exception) {
                ErrorDialog(e).showDialog()
            }
        }
    }
}