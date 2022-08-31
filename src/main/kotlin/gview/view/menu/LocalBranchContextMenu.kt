package gview.view.menu

import gview.model.branch.GvLocalBranch
import gview.resourceBundle
import gview.view.function.BranchFunction
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.SeparatorMenuItem
import org.jetbrains.annotations.NonNls

class LocalBranchContextMenu(private val model: GvLocalBranch): ContextMenu() {

    /* このブランチをチェックアウト */
    @NonNls
    private val checkOutMenuItem = GvMenuItem(
        text = resourceBundle().getString("LocalBranchContextMenu.Checkout"),
        iconLiteral = "mdi2s-source-branch",
        bold = true
    ) { BranchFunction.doCheckout(model) }

    /* 現在のブランチにマージ */
    @NonNls
    private val mergeMenuItem = GvMenuItem(
        text = resourceBundle().getString("LocalBranchContextMenu.Merge"),
        iconLiteral = "mdi2s-source-merge"
    ) { BranchFunction.doMerge(model) }

    /* 現在の変更をリベース */
    @NonNls
    private val rebaseMenuItem = GvMenuItem(
        text = resourceBundle().getString("LocalBranchContextMenu.Rebase"),
        iconLiteral = "mdi2s-source-branch-check"
    ) { BranchFunction.doRebase(model) }

    /* プル */
    @NonNls
    private val pullMenuItem = GvMenuItem(
        text = resourceBundle().getString("LocalBranchContextMenu.Pull"),
        iconLiteral = "mdi2s-source-pull"
    ) { BranchFunction.doPull(model) }

    /* プッシュ */
    @NonNls
    private val pushMenuItem = GvMenuItem(
        text = resourceBundle().getString("LocalBranchContextMenu.Push"),
        iconLiteral = "mdi2s-source-branch-sync"
    ) { BranchFunction.doPush(model) }

    /* 名前の変更 */
    @NonNls
    private val renameMenuItem = GvMenuItem(
        text = resourceBundle().getString("LocalBranchContextMenu.Rename"),
        iconLiteral = "mdi2s-source-branch-check"
    ) { BranchFunction.doRename(model) }

    /* 削除 */
    @NonNls
    private val removeMenuItem = GvMenuItem(
        text = resourceBundle().getString("LocalBranchContextMenu.Remove"),
        iconLiteral = "mdi2s-source-branch-remove"
    ) { BranchFunction.doRemove(model) }

    /**
     * 初期化
     */
    init {
        items.setAll(
            checkOutMenuItem,
            mergeMenuItem,
            rebaseMenuItem,
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
        checkOutMenuItem.isDisable = BranchFunction.canCheckout(model).not()
        mergeMenuItem.isDisable = BranchFunction.canMerge(model).not()
        rebaseMenuItem.isDisable = BranchFunction.canRebase(model).not()
        pushMenuItem.isDisable = BranchFunction.canPush(model).not()
        pullMenuItem.isDisable = BranchFunction.canPull(model).not()
        renameMenuItem.isDisable = BranchFunction.canRename(model).not()
        removeMenuItem.isDisable = BranchFunction.canRemove(model).not()
    }
}
