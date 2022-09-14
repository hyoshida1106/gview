package gview.view.menu

import gview.model.branch.GvRemoteBranch
import gview.resourceBundle
import gview.view.function.BranchFunction
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu


class RemoteBranchContextMenu(val model: GvRemoteBranch) : ContextMenu() {

    /* このブランチをチェックアウト */
    private val checkOutMenuItem = GvMenuItem(
        text = resourceBundle().getString("RemoteBranchContextMenu.Checkout"),
        iconLiteral = "mdi2s-source-branch-check",          // NON-NLS
    ) { BranchFunction.doCheckout(model) }

    /* 削除 */
    private val removeMenuItem = GvMenuItem(
        text = resourceBundle().getString("RemoteBranchContextMenu.Remove"),
        iconLiteral = "mdi2d-delete"                       // NON-NLS
    ) { BranchFunction.doRemove(model) }

    init {
        items.setAll(
            checkOutMenuItem,
            removeMenuItem
        )
        onShowing = EventHandler { onMyShowing() }
    }

    private fun onMyShowing() {
        checkOutMenuItem.isDisable = BranchFunction.canCheckout(model).not()
        removeMenuItem.isDisable = BranchFunction.canRemove(model).not()
    }
}
