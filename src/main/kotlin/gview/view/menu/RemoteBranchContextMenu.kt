package gview.view.menu

import gview.view.main.MainWindow
import gview.model.branch.GviewRemoteBranchModel
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.SeparatorMenuItem


class RemoteBranchContextMenu(val model: GviewRemoteBranchModel)
    : ContextMenu() {

    private val checkOutMenuItem = GviewMenuItem(
            text = "このリモートブランチをローカルへチェックアウトする",
            iconLiteral = "mdi-folder-star"
    ) { onCheckOut() }

    private val removeMenuItem   = GviewMenuItem(
            text = "このリモートブランチを削除する",
            iconLiteral = "mdi-delete-forever"
    ) { onRemove() }

    init {
        items.setAll(
            checkOutMenuItem,
            SeparatorMenuItem(),
            removeMenuItem
        )
        onShowing = EventHandler { onMyShowing() }
    }

    private fun onMyShowing() {
        checkOutMenuItem.isDisable = (model.localBranch != null)
    }

    private fun onCheckOut() {
        MainWindow.controller.runTask {
            model.branchList.checkoutRemoteBranch(model) }
    }

    private fun onRemove() {
        println("$model remove")
    }
}
