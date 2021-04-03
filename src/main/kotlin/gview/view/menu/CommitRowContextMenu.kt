package gview.view.menu

import gview.GvApplication
import gview.view.dialog.BranchNameDialog
import gview.view.dialog.BranchSelectDialog
import gview.view.dialog.ErrorDialog
import gview.view.dialog.MergeDialog
import gview.model.commit.GviewCommitDataModel
import javafx.event.EventHandler
import javafx.scene.control.ButtonType
import javafx.scene.control.ContextMenu
import java.lang.Exception

class CommitRowContextMenu(private val model: GviewCommitDataModel)
    : ContextMenu() {

    private val checkoutMenu = GviewMenuItem(
            text = "このコミットをチェックアウトする...",
            iconLiteral = "mdi-source-branch"
    ) { onCheckout() }

    private val createBranchMenu = GviewMenuItem(
        text = "このコミットからブランチを作成する...",
        iconLiteral = "mdi-source-branch"
    ) { onCreateBranch() }

    private val mergeMenu = GviewMenuItem(
            text = "このコミットをヘッドへマージする...",
            iconLiteral = "mdi-source-merge"
    ) { onMerge() }

    private val branches = model.localBranches.filter { !it.isCurrentBranch }

    init {
        items.setAll(
                checkoutMenu,
                createBranchMenu,
                mergeMenu
        )
        onShowing = EventHandler { onMyShowing() }
    }

    private fun onMyShowing() {
        when (branches.count()) {
            0 -> { checkoutMenu.isDisable = true }
            1 -> { checkoutMenu.text = "\"${branches[0].name}\"をチェックアウトする" }
        }
        mergeMenu.isDisable = model.isHead
    }

    private fun onCheckout() {
        try {
            val selectedBranch = if(branches.count() == 1) {
                branches[0]
            } else {
                BranchSelectDialog(branches).showDialog()
            }
            if(selectedBranch != null) {
                GvApplication.app.currentRepository.branches.checkoutLocalBranch(selectedBranch)
            }
        } catch(e: Exception) {
            ErrorDialog(e).showDialog()
        }
    }

    private fun onCreateBranch() {
        val dialog = BranchNameDialog()
        if(dialog.showDialog() == ButtonType.OK) {
            try {
                GvApplication.app.currentRepository.branches.createNewBranchFromCommit(
                        dialog.controller.newBranchName, model, dialog.controller.checkoutFlag)
            } catch(e: Exception) {
                ErrorDialog(e).showDialog()
            }
        }
    }

    private fun onMerge() {
        val dialog = MergeDialog()
        if(dialog.showDialog() == ButtonType.OK) {
            try {
                GvApplication.app.currentRepository.branches.mergeCommit(
                        model, dialog.controller.message.trim())
            } catch(e: Exception) {
                ErrorDialog(e).showDialog()
            }
        }
    }
}