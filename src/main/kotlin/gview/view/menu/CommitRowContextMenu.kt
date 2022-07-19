package gview.view.menu

import gview.model.GvRepository
import gview.view.dialog.BranchNameDialog
import gview.view.dialog.BranchSelectDialog
import gview.view.dialog.ErrorDialog
import gview.view.dialog.MergeDialog
import gview.model.commit.GvCommit
import javafx.event.EventHandler
import javafx.scene.control.ButtonType
import javafx.scene.control.ContextMenu
import java.lang.Exception

class CommitRowContextMenu(private val model: GvCommit)
    : ContextMenu() {

    private val checkoutMenu = GvMenuItem(
            text = "このコミットをチェックアウトする...",
            iconLiteral = "mdi-source-branch"
    ) { onCheckout() }

    private val createBranchMenu = GvMenuItem(
        text = "このコミットからブランチを作成する...",
        iconLiteral = "mdi-source-branch"
    ) { onCreateBranch() }

    private val mergeMenu = GvMenuItem(
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
            (if (branches.count() == 1) {
                branches[0]
            } else {
                BranchSelectDialog(branches).showDialog()
            })?.checkout()
        } catch (e: Exception) {
            ErrorDialog(e).showDialog()
        }
    }

    private fun onCreateBranch() {
        val dialog = BranchNameDialog()
        if(dialog.showDialog() == ButtonType.OK) {
            try {
                GvRepository.currentRepository?.branches?.createNewBranchFromCommit(
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
                GvRepository.currentRepository?.branches?.mergeCommit(
                        model, dialog.controller.message.trim())
            } catch(e: Exception) {
                ErrorDialog(e).showDialog()
            }
        }
    }
}