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
import javafx.scene.control.SeparatorMenuItem
import java.lang.Exception

class CommitRowContextMenu(private val model: GvCommit): ContextMenu() {

    /* このコミットをチェックアウトする... */
    private val checkoutMenu = GvMenuItem(
        text = resourceBundle().getString("CommitRowContextMenu.Checkout"),
        iconLiteral = "mdi2s-source-branch"                 // NON-NLS
    ) { checkoutFromThisCommit() }

    /* このコミットからブランチを作成する... */
    private val createBranchMenu = GvMenuItem(
        text = resourceBundle().getString("CommitRowContextMenu.CreateBranch"),
        iconLiteral = "mdi2s-source-branch"                 // NON-NLS
    ) { createNewBranchFromThisCommit() }

    /* このコミットをヘッドへマージする... */
    private val mergeMenu = GvMenuItem(
        text = resourceBundle().getString("CommitRowContextMenu.Merge"),
        iconLiteral = "mdi2s-source-merge"                  // NON-NLS
    ) { mergeThisCommitToHead() }

    /* このコミットにタグを作成する... */
    private val tagMenu = GvMenuItem(
        text = resourceBundle().getString("CommitRowContextMenu.Tag"),
        iconLiteral = "mdi2t-tag-outline"                   // NON-NLS
    ) { }

    /* リベース/インタラクティブなリベース... */
    private val rebaseMenu = GvMenuItem(
        text = resourceBundle().getString("CommitRowContextMenu.Rebase"),
        iconLiteral = "mdi2s-source-merge"                   // NON-NLS
    ) { }

    /* ここまでリセット */
    private val resetMenu = GvMenuItem(
        text = resourceBundle().getString("CommitRowContextMenu.Reset"),
        iconLiteral = "mdi2r-rewind"                        // NON-NLS
    ) { }

    /* リバース */
    private val reverseMenu = GvMenuItem(
        text = resourceBundle().getString("CommitRowContextMenu.Reverse"),
        iconLiteral = "mdi2r-restore"                       // NON-NLS
    ) { }

    /* パッチ生成 */
    private val patchMenu = GvMenuItem(
        text = resourceBundle().getString("CommitRowContextMenu.CreatePatch"),
        iconLiteral = "mdi2d-download-outline"              // NON-NLS
    ) { }

    /* チェリーピック */
    private val cherryPickMenu = GvMenuItem(
        text = resourceBundle().getString("CommitRowContextMenu.CherryPick"),
        iconLiteral = "mdi2s-source-commit-local"           // NON-NLS
    ) { }

    init {
        items.setAll(
            checkoutMenu,
            createBranchMenu,
            mergeMenu,
            tagMenu,
            rebaseMenu,
            SeparatorMenuItem(),
            resetMenu,
            reverseMenu,
            patchMenu,
            cherryPickMenu
        )
        onShowing = EventHandler { onMyShowing() }
    }

    private fun onMyShowing() {
        checkoutMenu.isDisable = true
        createBranchMenu.isDisable = false
        mergeMenu.isDisable = model.isHead
        tagMenu.isDisable = true
        rebaseMenu.isDisable = true
        resetMenu.isDisable = true
        reverseMenu.isDisable = true
        patchMenu.isDisable = true
        cherryPickMenu.isDisable = true
    }

    private fun checkoutFromThisCommit() {
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

    private fun createNewBranchFromThisCommit() {
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

    private fun mergeThisCommitToHead() {
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