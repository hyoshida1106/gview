package gview.view.menu

import gview.view.main.MainWindow
import gview.model.branch.GvRemoteBranch
import gview.resourceBundle
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import org.jetbrains.annotations.NonNls


class RemoteBranchContextMenu(val model: GvRemoteBranch)
    : ContextMenu() {

    /* このブランチをチェックアウト */
    @NonNls
    private val checkOutMenuItem = GvMenuItem(
        text = resourceBundle().getString("RemoteBranch.Checkout"),
        iconLiteral = "mdi2s-source-branch-check",
    ) { checkoutRemoteBranch() }

    /* 削除 */
    @NonNls
    private val removeMenuItem = GvMenuItem(
        text = resourceBundle().getString("RemoteBranch.Remove"),
        iconLiteral = "mdi2d-delete"
    ) { removeRemoteBranch() }

    init {
        items.setAll(
            checkOutMenuItem,
            removeMenuItem
        )
        onShowing = EventHandler { onMyShowing() }
    }

    private fun onMyShowing() {
        checkOutMenuItem.isDisable = (model.localBranch.get() != null)
        removeMenuItem.isDisable = true
    }

    private fun checkoutRemoteBranch() {
        MainWindow.controller.runTask {
            model.branchList.checkoutRemoteBranch(model) }
    }

    private fun removeRemoteBranch() {
    }
}
