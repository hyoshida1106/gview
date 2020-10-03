package gview.gui.menu

import gview.gui.dialog.RemoveLocalBranchDialog
import gview.gui.framework.GviewMenuItem
import gview.gui.main.MainWindow
import gview.model.branch.GviewLocalBranchModel
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.SeparatorMenuItem


class LocalBranchContextMenu(private val model: GviewLocalBranchModel): ContextMenu() {

    private val checkOutMenuItem = GviewMenuItem(
            "チェックアウト"
    ) { onCheckOut() }

    private val removeMenuItem   = GviewMenuItem(
            "ローカルブランチを削除"
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
        checkOutMenuItem.isDisable = (model.isCurrentRepository)
        removeMenuItem.isDisable = (model.isCurrentRepository)
    }

    private fun onCheckOut() {
        MainWindow.controller.runTask { model.branchList.checkoutLocalBranch(model) }
    }

    private fun onRemove() {
        val dialog = RemoveLocalBranchDialog("${model.name}を削除しますか")
        if(dialog.showDialog()) {
            MainWindow.controller.runTask { model.branchList.removeLocalBranch(model, dialog.forceRemove) }
        }
    }
}
