package gview.gui.main

import gview.gui.dialog.SelectCommitFilesDialog
import gview.gui.dialog.SelectStageFilesDialog
import gview.gui.dialog.SelectUnStageFilesDialog
import gview.gui.framework.GviewBaseMenu
import gview.model.GviewRepositoryModel
import javafx.fxml.FXML
import javafx.scene.control.ButtonType
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem

object WorkTreeMenu: GviewBaseMenu<WorkTreeMenuCtrl>("/view/WorkTreeMenu.fxml")

class WorkTreeMenuCtrl {

    @FXML private lateinit var workTreeMenu: Menu
    @FXML private lateinit var workTreeStageMenu: MenuItem
    @FXML private lateinit var workTreeUnStageMenu: MenuItem
    @FXML private lateinit var workTreeCommitMenu: MenuItem

    @FXML private fun onShowingMenu() {
        val headerData = GviewRepositoryModel.currentRepository.headerFiles
        val stagedFileNumber = headerData.stagedFiles?.size?:0
        val changedFileNumber = headerData.changedFiles?.size?:0
        workTreeStageMenu.isDisable = changedFileNumber == 0
        workTreeUnStageMenu.isDisable = stagedFileNumber == 0
        workTreeCommitMenu.isDisable = stagedFileNumber == 0
    }

    @FXML fun onStageMenu() {
        val dialog = SelectStageFilesDialog()
        if(dialog.showDialog() == ButtonType.OK) {
            println("${dialog.selectedFiles}")
        }
    }

    @FXML fun onUnStageMenu() {
        val dialog = SelectUnStageFilesDialog()
        if(dialog.showDialog() == ButtonType.OK) {
            println("${dialog.selectedFiles}")
        }
    }

    @FXML fun onCommitMenu() {
        val dialog = SelectCommitFilesDialog()
        if(dialog.showDialog() == ButtonType.OK) {
            println("${dialog.selectedFiles}")
        }
    }
}

