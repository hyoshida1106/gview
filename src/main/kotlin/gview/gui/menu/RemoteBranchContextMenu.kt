package gview.gui.menu

import gview.gui.framework.GviewCommonDialog
import gview.gui.framework.GviewMenuItem
import gview.model.branch.GviewRemoteBranchModel
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.SeparatorMenuItem
import java.lang.Exception


class RemoteBranchContextMenu(val model: GviewRemoteBranchModel): ContextMenu() {

    private val checkOutMenuItem = GviewMenuItem(
            "ローカルへチェックアウト"
    ) { onCheckOut() }

    private val removeMenuItem   = GviewMenuItem(
            "リモートブランチを削除"
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
        try {
            model.branchList.checkoutRemoteBranch(model)
        } catch(e: Exception) {
            GviewCommonDialog.errorDialog(e)
        }
    }

    private fun onRemove() {
        println("$model remove")
    }
}
