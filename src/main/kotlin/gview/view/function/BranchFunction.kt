package gview.view.function

import gview.model.branch.GvLocalBranch
import gview.model.branch.GvRemoteBranch
import gview.resourceBundle
import gview.view.dialog.*
import gview.view.main.MainWindow
import javafx.scene.control.ButtonType
import org.eclipse.jgit.api.errors.NotMergedException

object BranchFunction {

    // Checkout - Local
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

    // Checkout - Remote
    fun canCheckout(branch: GvRemoteBranch?): Boolean {
        return if(branch != null) {
            branch.localBranch.get() == null
        } else {
            false
        }
    }
    fun doCheckout(branch: GvRemoteBranch) {
        try {
            MainWindow.runTask { branch.checkout() }
        } catch (e: Exception) {
            ErrorDialog(e).showDialog()
        }
    }

    // Pull
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

    // Push
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

    // Remove Local Branch
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

    // Remove Remote Branch
    fun canRemove(branch: GvRemoteBranch?): Boolean {
        return if(branch != null) {
            branch.localBranch.get() == null
        } else {
            false
        }
    }
    fun doRemove(branch: GvRemoteBranch) {
        val dialog = RemoveRemoteBranchDialog(
            String.format(resourceBundle().getString("Message.ConfirmToRemove"), branch.name))
        if (dialog.showDialog()) {
            try {
                MainWindow.runTask { branch.remove() }
            } catch (e:NotMergedException) {
                ErrorDialog(resourceBundle().getString("Message.BranchNotMerged").format(branch.name)).showDialog()
            } catch (e: Exception) {
                ErrorDialog(e).showDialog()
            }
        }
    }

    fun canMerge(branch: GvLocalBranch?): Boolean {
        return branch?.isCurrentBranch?.not() ?: false
    }
    fun doMerge(branch: GvLocalBranch) {
        val dialog = MergeDialog()
        if (dialog.showDialog() != ButtonType.OK) return
        try {
            MainWindow.runTask { branch.mergeToHead(dialog.message) }
        } catch (e: Exception) {
            ErrorDialog(e).showDialog()
        }
    }

    fun canRename(branch: GvLocalBranch?): Boolean {
        return branch != null
    }

    fun doRename(branch: GvLocalBranch) {
        val dialog = RenameBranchDialog(branch.name)
        if(dialog.showDialog() != ButtonType.OK) return
        try {
            MainWindow.runTask { branch.rename(dialog.controller.newBranchName) }
        } catch (e: Exception) {
            ErrorDialog(e).showDialog()
        }
    }

    fun canRebase(branch: GvLocalBranch?): Boolean {
        return branch?.isCurrentBranch?.not() ?: false
    }

    fun doRebase(branch: GvLocalBranch) {
        val message = resourceBundle().getString("Message.ConfirmToRebase").format(
            branch.repository.currentBranch, branch.name)
        if (ConfirmationDialog(ConfirmationDialog.ConfirmationType.YesNo, message).showDialog()) {
            try {
                branch.rebase()
            } catch (e: Exception) {
                ErrorDialog(e).showDialog()
            }
        }
    }
}