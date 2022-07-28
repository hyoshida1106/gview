package gview.view.menu

import gview.model.GvRepository
import gview.view.dialog.CreateBranchByNameDialog
import gview.view.dialog.ErrorDialog
import gview.view.dialog.MergeDialog
import gview.model.commit.GvCommit
import gview.resourceBundle
import javafx.event.EventHandler
import javafx.scene.control.ButtonType
import javafx.scene.control.ContextMenu
import org.jetbrains.annotations.NonNls
import java.lang.Exception

class CommitRowContextMenu(private val model: GvCommit): ContextMenu() {

    @NonNls
    private val checkoutMenu = GvMenuItem(
        text = resourceBundle().getString("CommitData.Checkout"),
        iconLiteral = "mdi2s-source-commit"
    ) { onCheckout() }

    @NonNls
    private val createBranchMenu = GvMenuItem(
        text = resourceBundle().getString("CommitData.CreateBranch"),
        iconLiteral = "mdi2s-source-branch"
    ) { onCreateBranch() }

    @NonNls
    private val mergeMenu = GvMenuItem(
        text = resourceBundle().getString("CommitData.Merge"),
        iconLiteral = "mdi2s-source-merge"
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
        checkoutMenu.isDisable = true
        mergeMenu.isDisable = model.isHead
    }

    private fun onCheckout() {
//        try {
//            (if (branches.count() == 1) {
//                branches[0]
//            } else {
//                BranchSelectDialog(branches).showDialog()
//            })?.checkout()
//        } catch (e: Exception) {
//            ErrorDialog(e).showDialog()
//        }
    }

    private fun onCreateBranch() {
        val dialog = CreateBranchByNameDialog()
        if (dialog.showDialog() != ButtonType.OK) return
        try {
            GvRepository.currentRepository?.branches?.createNewBranchFromCommit(
                dialog.controller.newBranchName, model, dialog.controller.checkoutFlag
            )
        } catch (e: Exception) {
            ErrorDialog(e).showDialog()
        }
    }

    private fun onMerge() {
        val dialog = MergeDialog()
        if (dialog.showDialog() != ButtonType.OK) return
        try {
            GvRepository.currentRepository?.branches?.mergeCommit(
                model, dialog.controller.message.trim()
            )
        } catch (e: Exception) {
            ErrorDialog(e).showDialog()
        }
    }
}