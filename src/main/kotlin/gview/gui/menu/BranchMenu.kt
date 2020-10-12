package gview.gui.menu

import gview.gui.dialog.CreateBranchDialog
import gview.gui.dialog.CreateBranchDialogCtrl
import gview.gui.framework.GviewMenuItem
import gview.model.GviewRepositoryModel
import javafx.event.EventHandler
import javafx.scene.control.ButtonType
import javafx.scene.control.Menu
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination

class BranchMenu
    : Menu("ブランチ(_B)") {

    private val checkoutMenu = GviewMenuItem(
            text= "チェックアウト(_C)",
            iconLiteral = "mdi-folder-star"
    ) { onCheckOut() }

    private val pushMenu = GviewMenuItem(
            text= "プッシュ(_P)",
            accelerator = KeyCodeCombination(
                    KeyCode.P,
                    KeyCombination.SHORTCUT_DOWN,
                    KeyCombination.SHIFT_DOWN),
            iconLiteral = "mdi-folder-upload"
    ) { onPush() }

    private val pullMenu = GviewMenuItem(
            text = "プル(_L)",
            accelerator = KeyCodeCombination(
                    KeyCode.L,
                    KeyCombination.SHORTCUT_DOWN,
                    KeyCombination.SHIFT_DOWN),
            iconLiteral = "mdi-folder-download"
    ) { onPull() }

    private val createMenu = GviewMenuItem(
            text = "新規作成(_)...",
            iconLiteral = "mdi-folder-plus"
    ) { onCreate() }

    private val renameMenu = GviewMenuItem(
            text = "名称変更(_R)...",
            iconLiteral = "mdi-folder-move"
    ) { onRename() }

    private val removeMenu = GviewMenuItem(
            text = "削除(_D)",
            iconLiteral = "mdi-folder-remove"
    ) { onRemove() }


    private val branches = GviewRepositoryModel.currentRepository.branches

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
        val repositoryInvalid = GviewRepositoryModel.currentRepository.jgitRepository == null
        checkoutMenu.isDisable = repositoryInvalid
        pushMenu.isDisable = repositoryInvalid
        pullMenu.isDisable = repositoryInvalid
        createMenu.isDisable = repositoryInvalid
        renameMenu.isDisable = repositoryInvalid
        removeMenu.isDisable = repositoryInvalid
    }

    private fun onCheckOut() {
    }

    private fun onPush() {
    }

    private fun onPull() {
    }

    private fun onCreate() {
        val dialog = CreateBranchDialog()
        if(dialog.showDialog() == ButtonType.OK) {
            val branches = GviewRepositoryModel.currentRepository.branches
            val branchName = dialog.controller.newBranchName
            when (dialog.controller.startPoint) {
                CreateBranchDialogCtrl.BranchStartPoint.FromHead -> {
                    branches.createNewBranchFromHead(branchName)
                }
                CreateBranchDialogCtrl.BranchStartPoint.ByOtherBranch -> {
                    branches.createNewBranchFromOtherBranch(
                            branchName,
                            dialog.controller.selectedBranch!!)
                }
                CreateBranchDialogCtrl.BranchStartPoint.ByCommit -> {
                    branches.createNewBranchFromCommit(
                            branchName,
                            dialog.controller.selected!!)
                }
            }
        }
    }

    private fun onRename() {
    }

    private fun onRemove() {
    }

}

