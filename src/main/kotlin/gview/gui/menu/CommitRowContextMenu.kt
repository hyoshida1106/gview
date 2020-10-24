package gview.gui.menu

import gview.GviewApp
import gview.gui.dialog.BranchNameDialog
import gview.gui.dialog.BranchSelectDialog
import gview.gui.dialog.ErrorDialog
import gview.gui.dialog.MergeDialog
import gview.gui.framework.GviewMenuItem
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
    }

    private fun onCheckout() {
        try {
            val selectedBranch = if(branches.count() == 1) {
                branches[0]
            } else {
                BranchSelectDialog(branches).showDialog()
            }
            if(selectedBranch != null) {
                GviewApp.currentRepository.branches.checkoutLocalBranch(selectedBranch)
            }
        } catch(e: Exception) {
            ErrorDialog(e).showDialog()
        }
    }

    private fun onCreateBranch() {
        val dialog = BranchNameDialog()
        if(dialog.showDialog() == ButtonType.OK) {
            try {
                GviewApp.currentRepository.branches.createNewBranchFromCommit(
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
                GviewApp.currentRepository.branches.mergeCommit(
                        model, dialog.controller.message)
            } catch(e: Exception) {
                ErrorDialog(e).showDialog()
            }
        }
    }
}