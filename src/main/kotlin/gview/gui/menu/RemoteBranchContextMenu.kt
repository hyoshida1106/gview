package gview.gui.menu

import gview.gui.framework.GviewMenuItem
import gview.model.branch.GviewRemoteBranchModel
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.SeparatorMenuItem


class RemoteBranchContextMenu(val model: GviewRemoteBranchModel): ContextMenu() {

    private val checkOutMenuItem = GviewMenuItem("ローカルへチェックアウト") { onCheckOut() }
    private val removeMenuItem   = GviewMenuItem("リモートブランチを削除") { onRemove() }

    init {
        items.setAll(
            checkOutMenuItem,
            SeparatorMenuItem(),
            removeMenuItem
        )
        onShowing = EventHandler { onMyShowing() }
    }

    private fun onMyShowing() {
        removeMenuItem.isDisable = true
    }

    private fun onCheckOut() {
        println("$model checkout")
    }

    private fun onRemove() {
        println("$model remove")
    }
}
