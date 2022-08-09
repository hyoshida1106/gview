package gview.view.function

import gview.model.branch.GvLocalBranch
import gview.view.dialog.ErrorDialog
import gview.view.main.MainWindow

object BranchFunction {

    fun canCheckout(branch: GvLocalBranch?): Boolean {
        return branch?.isCurrentBranch?.not() ?: false
    }

    fun doCheckout(branch: GvLocalBranch?) {
        if(branch == null) return
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
        if(branch == null) return
        try {
            MainWindow.runTask { branch.pull() }
        } catch (e: Exception) {
            ErrorDialog(e).showDialog()
        }
    }
}