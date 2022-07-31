package gview.view.menu

import gview.view.dialog.RemoveLocalBranchDialog
import gview.view.main.MainWindow
import gview.model.branch.GvLocalBranch
import gview.resourceBundle
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.SeparatorMenuItem
import org.jetbrains.annotations.NonNls

class LocalBranchContextMenu(private val model: GvLocalBranch): ContextMenu() {

    /* このブランチをチェックアウト */
    @NonNls
    private val checkOutMenuItem = GvMenuItem(
        text = resourceBundle().getString("LocalBranch.Checkout"),
        iconLiteral = "mdi2s-source-branch",
        bold = true
    ) { checkoutLocalBranch() }

    /* 現在のブランチにマージ */
    @NonNls
    private val mergeMenuItem = GvMenuItem(
        text = resourceBundle().getString("LocalBranch.Merge"),
        iconLiteral = "mdi2s-source-merge"
    ) {  }

    /* 現在の変更をリベース */
    @NonNls
    private val rebaseMenuItem = GvMenuItem(
        text = resourceBundle().getString("LocalBranch.Rebase"),
        iconLiteral = "mdi2s-source-branch-check"
    ) {  }

    /* フェッチ */
    @NonNls
    private val fetchMenuItem = GvMenuItem(
        text = resourceBundle().getString("LocalBranch.Fetch"),
        iconLiteral = "mdi2s-source-branch-refresh"
    ) {  }

    /* プル */
    @NonNls
    private val pullMenuItem = GvMenuItem(
        text = resourceBundle().getString("LocalBranch.Pull"),
        iconLiteral = "mdi2s-source-pull"
    ) {  }

    /* プッシュ */
    @NonNls
    private val pushMenuItem = GvMenuItem(
        text = resourceBundle().getString("LocalBranch.Push"),
        iconLiteral = "mdi2s-source-branch-sync"
    ) {  }

    /* 名前の変更 */
    @NonNls
    private val renameMenuItem = GvMenuItem(
        text = resourceBundle().getString("LocalBranch.Rename"),
        iconLiteral = "mdi2s-source-branch-check"
    ) { }

    /* 削除 */
    @NonNls
    private val removeMenuItem = GvMenuItem(
        text = resourceBundle().getString("LocalBranch.Remove"),
        iconLiteral = "mdi2s-source-branch-remove"
    ) { removeLocalBranch() }

    /**
     * 初期化
     */
    init {
        items.setAll(
            checkOutMenuItem,
            mergeMenuItem,
            rebaseMenuItem,
            SeparatorMenuItem(),
            fetchMenuItem,
            SeparatorMenuItem(),
            pushMenuItem,
            pullMenuItem,
            SeparatorMenuItem(),
            renameMenuItem,
            removeMenuItem
        )
        onShowing = EventHandler { onMyShowing() }
    }

    /**
     * メニュー表示時処理
     */
    private fun onMyShowing() {
        checkOutMenuItem.isDisable = model.isCurrentBranch
        mergeMenuItem.isDisable = true
        rebaseMenuItem.isDisable = true
        fetchMenuItem.isDisable = true
        pushMenuItem.isDisable = true
        pullMenuItem.isDisable = true
        renameMenuItem.isDisable = true
        removeMenuItem.isDisable = model.isCurrentBranch
    }

    private fun checkoutLocalBranch() {
        MainWindow.controller.runTask { model.checkout() }
    }

    private fun removeLocalBranch() {
        val dialog = RemoveLocalBranchDialog(String.format(resourceBundle().getString("Message.ConfirmToRemove"), model.name))
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
