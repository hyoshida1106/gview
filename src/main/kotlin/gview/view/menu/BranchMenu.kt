package gview.view.menu

import gview.model.GvRepository
import gview.resourceBundle
import gview.view.dialog.CreateBranchDialog
import gview.view.dialog.CreateBranchDialogCtrl
import javafx.event.EventHandler
import javafx.scene.control.ButtonType
import javafx.scene.control.Menu
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import org.jetbrains.annotations.NonNls

class BranchMenu : Menu(resourceBundle().getString("BranchMenu")) {

    @NonNls
    private val checkoutLocalBranchMenu = GvMenuItem(
        /* チェックアウト */
        text = "test",
        iconLiteral = "mdi2f-folder-star"
    ) { onCheckOut() }

    private val checkoutMenu = GvSubMenu(
        text = resourceBundle().getString("BranchCheckout"),
        iconLiteral = "mdi2f-folder-star",
        subMenuList = arrayOf( checkoutLocalBranchMenu )
    )

    @NonNls
    private val pushMenu = GvMenuItem(
        text = resourceBundle().getString("BranchPush"),
        accelerator = KeyCodeCombination(
            KeyCode.P,
            KeyCombination.SHORTCUT_DOWN,
            KeyCombination.SHIFT_DOWN
        ),
        iconLiteral = "mdi2f-folder-upload"
    ) { onPush() }

    @NonNls
    private val pullMenu = GvMenuItem(
        text = resourceBundle().getString("BranchPull"),
        accelerator = KeyCodeCombination(
            KeyCode.L,
            KeyCombination.SHORTCUT_DOWN,
            KeyCombination.SHIFT_DOWN
        ),
        iconLiteral = "mdi2f-folder-download"
    ) { onPull() }

    @NonNls
    private val createMenu = GvMenuItem(
        text = resourceBundle().getString("BranchCreate"),
        iconLiteral = "mdi2f-folder-plus"
    ) { onCreate() }

    @NonNls
    private val renameMenu = GvMenuItem(
        text = resourceBundle().getString("BranchRename"),
        iconLiteral = "mdi2f-folder-move"
    ) { onRename() }

    @NonNls
    private val removeMenu = GvMenuItem(
        text = resourceBundle().getString("BranchRemove"),
        iconLiteral = "mdi2f-folder-remove"
    ) { onRemove() }

    init {
        items.setAll(
            checkoutMenu,
            pushMenu,
            pullMenu,
            SeparatorMenuItem(),
            createMenu,
            renameMenu,
            removeMenu
        )
        onShowing = EventHandler { onShowingMenu() }
    }

    //メニュー表示
    private fun onShowingMenu() {
        val repositoryInvalid = (GvRepository.currentRepository == null)
        checkoutMenu.isDisable = repositoryInvalid
        pushMenu.isDisable = repositoryInvalid
        pullMenu.isDisable = repositoryInvalid
        createMenu.isDisable = repositoryInvalid
        renameMenu.isDisable = repositoryInvalid
        removeMenu.isDisable = repositoryInvalid
    }

    /**
     * ブランチのチェックアウト
     */
    private fun onCheckOut() {
    }

    private fun onPush() {
    }

    private fun onPull() {
    }

    /**
     * ブランチの新規作成
     */
    private fun onCreate() {
        val branches = GvRepository.currentRepository?.branches ?: return
        val dialog = CreateBranchDialog()
        if (dialog.showDialog() != ButtonType.OK) return
        val branchName = dialog.controller.newBranchName
        val checkout = dialog.controller.checkoutFlag
        when (dialog.controller.startPoint) {
            CreateBranchDialogCtrl.BranchStartPoint.FromHead -> {
                branches.createNewBranchFromHead(branchName, checkout)
            }
            CreateBranchDialogCtrl.BranchStartPoint.ByOtherBranch -> {
                branches.createNewBranchFromOtherBranch(
                    branchName,
                    dialog.controller.selectedBranch!!,
                    checkout
                )
            }
            CreateBranchDialogCtrl.BranchStartPoint.ByCommit -> {
                branches.createNewBranchFromCommit(
                    branchName,
                    dialog.controller.selected!!,
                    checkout
                )
            }
        }
    }

    private fun onRename() {
    }

    private fun onRemove() {
    }

}

