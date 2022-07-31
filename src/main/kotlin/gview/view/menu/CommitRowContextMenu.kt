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
import org.jetbrains.annotations.NonNls
import java.lang.Exception

class CommitRowContextMenu(private val model: GvCommit): ContextMenu() {

    /* このコミットをチェックアウトする... */
    @NonNls
    private val checkoutMenu = GvMenuItem(
        text = resourceBundle().getString("CommitData.Checkout"),
        iconLiteral = "mdi2s-source-branch"
    ) { checkoutFromThisCommit() }

    /* このコミットからブランチを作成する... */
    @NonNls
    private val createBranchMenu = GvMenuItem(
        text = resourceBundle().getString("CommitData.CreateBranch"),
        iconLiteral = "mdi2s-source-branch"
    ) { createNewBranchFromThisCommit() }

    /* このコミットをヘッドへマージする... */
    @NonNls
    private val mergeMenu = GvMenuItem(
        text = resourceBundle().getString("CommitData.Merge"),
        iconLiteral = "mdi2s-source-merge"
    ) { mergeThisCommitToHead() }

    /* このコミットにタグを作成する... */
    @NonNls
    private val tagMenu = GvMenuItem(
        text = resourceBundle().getString("CommitData.Tag"),
        iconLiteral = "mdi2t-tag-outline"
    ) { }

    /* リベース/インタラクティブなリベース... */
    @NonNls
    private val rebaseMenu = GvMenuItem(
        text = resourceBundle().getString("CommitData.Rebase"),
        iconLiteral = "mdi2s-source-merge"
    ) { }

    /* ここまでリセット */
    @NonNls
    private val resetMenu = GvMenuItem(
        text = resourceBundle().getString("CommitData.Reset"),
        iconLiteral = "mdi2r-rewind"
    ) { }

    /* リバース */
    @NonNls
    private val reverseMenu = GvMenuItem(
        text = resourceBundle().getString("CommitData.Reverse"),
        iconLiteral = "mdi2r-restore"
    ) { }

    /* パッチ生成 */
    @NonNls
    private val patchMenu = GvMenuItem(
        text = resourceBundle().getString("CommitData.CreatePatch"),
        iconLiteral = "mdi2d-download-outline"
    ) { }

    /* チェリーピック */
    @NonNls
    private val cherryPickMenu = GvMenuItem(
        text = resourceBundle().getString("CommitData.CherryPick"),
        iconLiteral = "mdi2s-source-commit-local"
    ) { }

//    private val branches = model.localBranches.filter { !it.isCurrentBranch }

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