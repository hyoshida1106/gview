package gview.view.menu

import gview.model.GvRepository
import gview.view.dialog.BranchNameDialog
import gview.view.dialog.ErrorDialog
import gview.view.dialog.RemoveLocalBranchDialog
import gview.view.main.MainWindow
import gview.model.branch.GvLocalBranch
import javafx.event.EventHandler
import javafx.scene.control.ButtonType
import javafx.scene.control.ContextMenu
import javafx.scene.control.SeparatorMenuItem
import java.lang.Exception


class LocalBranchContextMenu(private val model: GvLocalBranch)
    : ContextMenu() {

    private val checkOutMenuItem = GviewMenuItem(
            text = "このブランチをカレントブランチにする" ,
            iconLiteral = "mdi-download"
    ) { onCheckOut() }

    private val createBranchMenuItem = GviewMenuItem(
            text = "このブランチから新たなブランチを作成する..." ,
            iconLiteral = "mdi-source-branch"
    ) { onCreateNewBranch() }

    private val removeMenuItem   = GviewMenuItem(
            text = "このブランチを削除する",
            iconLiteral = "mdi-delete-forever"
    ) { onRemove() }

    init {
        items.setAll(
            checkOutMenuItem,
            createBranchMenuItem,
            SeparatorMenuItem(),
            removeMenuItem
        )
        onShowing = EventHandler { onMyShowing() }
    }

    private fun onMyShowing() {
        checkOutMenuItem.isDisable = (model.isCurrentBranch)
        removeMenuItem.isDisable = (model.isCurrentBranch)
    }

    private fun onCheckOut() {
        MainWindow.controller.runTask {
            model.branchList.checkoutLocalBranch(
                    model) }
    }

    private fun onCreateNewBranch() {
        val dialog = BranchNameDialog()
        if(dialog.showDialog() == ButtonType.OK) {
            try {
                GvRepository.currentRepository?.branches?.createNewBranchFromOtherBranch(
                        dialog.controller.newBranchName, model, dialog.controller.checkoutFlag)
            } catch(e: Exception) {
                ErrorDialog(e).showDialog()
            }
        }

    }

    private fun onRemove() {
        val dialog = RemoveLocalBranchDialog("${model.name}を削除しますか")
        if(dialog.showDialog()) {
            MainWindow.controller.runTask {
                model.branchList.removeLocalBranch(
                        model,
                        dialog.forceRemove) }
        }
    }
}
