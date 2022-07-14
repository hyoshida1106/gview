package gview.view.menu

import gview.view.main.MainWindow
import gview.model.branch.GvRemoteBranch
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.SeparatorMenuItem


class RemoteBranchContextMenu(val model: GvRemoteBranch)
    : ContextMenu() {

    private val checkOutMenuItem = GvMenuItem(
            text = "このリモートブランチをローカルへチェックアウトする",
            iconLiteral = "mdi-folder-star"
    ) { onCheckOut() }

    private val removeMenuItem   = GvMenuItem(
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
        checkOutMenuItem.isDisable = (model.localBranch.get() != null)
    }

    private fun onCheckOut() {
        MainWindow.controller.runTask {
            model.branchList.checkoutRemoteBranch(model) }
    }

    private fun onRemove() {
        println("$model remove")
    }
}
