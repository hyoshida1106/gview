package gview.gui.main

import gview.conf.ConfigUserInfo
import gview.gui.dialog.SelectCommitFilesDialog
import gview.gui.dialog.SelectStageFilesDialog
import gview.gui.dialog.SelectUnStageFilesDialog
import gview.gui.dialog.UserNameDialog
import gview.gui.framework.GviewBaseMenu
import gview.gui.framework.GviewCommonDialog
import gview.model.GviewRepositoryModel
import javafx.fxml.FXML
import javafx.scene.control.ButtonType
import javafx.scene.control.MenuItem

object WorkTreeMenu: GviewBaseMenu<WorkTreeMenuCtrl>("/view/WorkTreeMenu.fxml")

class WorkTreeMenuCtrl {

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
            try {
                val headerData = GviewRepositoryModel.currentRepository.headerFiles
                headerData.stageFiles(dialog.selectedFiles)
            } catch(e: Exception) {
                GviewCommonDialog.errorDialog(e)
            }
        }
    }

    @FXML fun onUnStageMenu() {
        val dialog = SelectUnStageFilesDialog()
        if(dialog.showDialog() != ButtonType.OK) {
            try {
                val headerData = GviewRepositoryModel.currentRepository.headerFiles
                headerData.unStageFiles(dialog.selectedFiles)
            } catch(e: Exception) {
                GviewCommonDialog.errorDialog(e)
            }
        }
    }

    @FXML fun onCommitMenu() {
        //ユーザ名とメールアドレスが未入力ならば入力する
        while (ConfigUserInfo.userName.isEmpty() || ConfigUserInfo.mailAddr.isEmpty()) {
            val dialog = UserNameDialog(ConfigUserInfo.userName, ConfigUserInfo.mailAddr)
            if(dialog.showDialog() != ButtonType.OK) {
                return
            }
            ConfigUserInfo.userName = dialog.userName
            ConfigUserInfo.mailAddr = dialog.mailAddr
            ConfigUserInfo.saveToFile()
        }
        //対象ファイルを選択する
        val dialog = SelectCommitFilesDialog()
        if (dialog.showDialog() != ButtonType.OK) {
            return
        }
        //コミットを実行する
        try {
            val headerData = GviewRepositoryModel.currentRepository.headerFiles
            headerData.commitFiles(dialog.selectedFiles, dialog.message)
        } catch (e: Exception) {
            GviewCommonDialog.errorDialog(e)
        }
    }
}

