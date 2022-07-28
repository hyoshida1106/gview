package gview.view.menu

import gview.view.dialog.RemoveLocalBranchDialog
import gview.view.main.MainWindow
import gview.model.branch.GvLocalBranch
import gview.resourceBundle
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.SeparatorMenuItem
import org.jetbrains.annotations.NonNls

class LocalBranchContextMenu(private val model: GvLocalBranch)
    : ContextMenu() {

    /* このブランチをカレントブランチにする */
    @NonNls
    private val checkOutMenuItem = GvMenuItem(
        text = resourceBundle().getString("CheckoutLocalBranch"),
        iconLiteral = "mdi2d-download",
        bold = true
    ) { checkoutLocalBranch() }

    /* このブランチの名称を変更する */
    @NonNls
    private val renameMenuItem = GvMenuItem(
        text = resourceBundle().getString("RenameLocalBranch"),
        iconLiteral = "mdi2r-rename-box"
    ) { renameLocalBranch() }

    /* このブランチを削除する */
    @NonNls
    private val removeMenuItem = GvMenuItem(
        text = resourceBundle().getString("RemoveLocalBranch"),
        iconLiteral = "mdi2d-delete-forever"
    ) { removeLocalBranch() }

    /**
     * 初期化
     */
    init {
        items.setAll(
            checkOutMenuItem,
            renameMenuItem,
            SeparatorMenuItem(),
            removeMenuItem
        )
        onShowing = EventHandler { onMyShowing() }
    }

    /**
     * メニュー表示時処理
     */
    private fun onMyShowing() {
        checkOutMenuItem.isDisable = (model.isCurrentBranch)
        removeMenuItem.isDisable = (model.isCurrentBranch)
    }

    private fun checkoutLocalBranch() {
        MainWindow.controller.runTask { model.checkout() }
    }

    private fun renameLocalBranch() {

    }

    private fun removeLocalBranch() {
        val dialog = RemoveLocalBranchDialog(String.format(resourceBundle().getString("ConfirmToRemove"), model.name))
        if (dialog.showDialog()) {
            MainWindow.controller.runTask {
                model.branchList.removeLocalBranch(
                    model,
                    dialog.forceRemove
                )
            }
        }
    }
}
