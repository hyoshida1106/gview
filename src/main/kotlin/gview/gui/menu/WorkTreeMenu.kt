package gview.gui.menu

import gview.conf.ConfigUserInfo
import gview.gui.dialog.SelectCommitFilesDialog
import gview.gui.dialog.SelectStageFilesDialog
import gview.gui.dialog.SelectUnStageFilesDialog
import gview.gui.dialog.UserNameDialog
import gview.gui.framework.GviewCommonDialog
import gview.gui.framework.GviewMenuItem
import gview.model.GviewRepositoryModel
import javafx.event.EventHandler
import javafx.scene.control.ButtonType
import javafx.scene.control.Menu
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination

class WorkTreeMenu: Menu("ワークツリー(_W)") {

    private val stageMenu = GviewMenuItem(
            text = "ステージ(_S)...",
            accelerator = KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN),
            iconLiteral = "mdi-arrow-up-bold-circle-outline"
    ) { doStageCommand() }

    private val unstageMenu = GviewMenuItem(
            text = "アンステージ(_U)...",
            accelerator = KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN),
            iconLiteral = "mdi-arrow-down-bold-circle-outline"
    ) { doUnStageCommand() }

    private val commitMenu = GviewMenuItem(
            text = "コミット(_C)...",
            accelerator = KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN),
            iconLiteral = "mdi-checkbox-marked-circle-outline"
    ) { doCommitCommand() }

    init {
        items.setAll(
                stageMenu,
                unstageMenu,
                SeparatorMenuItem(),
                commitMenu
        )
        onShowing = EventHandler { onShowingMenu() }
    }

    private fun onShowingMenu() {
        val headerData = GviewRepositoryModel.currentRepository.headerFiles
        val stagedFileNumber = headerData.stagedFiles?.size?:0
        val changedFileNumber = headerData.changedFiles?.size?:0
        stageMenu.isDisable = changedFileNumber == 0
        unstageMenu.isDisable = stagedFileNumber == 0
        commitMenu.isDisable = stagedFileNumber == 0
    }

    companion object {

        fun doStageCommand() {
            val dialog = SelectStageFilesDialog()
            if (dialog.showDialog() == ButtonType.OK) {
                try {
                    GviewRepositoryModel.currentRepository.headerFiles.stageFiles(dialog.selectedFiles)
                } catch (e: Exception) {
                    GviewCommonDialog.errorDialog(e)
                }
            }
        }

        fun doUnStageCommand() {
            val dialog = SelectUnStageFilesDialog()
            if (dialog.showDialog() == ButtonType.OK) {
                try {
                    GviewRepositoryModel.currentRepository.headerFiles.unStageFiles(dialog.selectedFiles)
                } catch (e: Exception) {
                    GviewCommonDialog.errorDialog(e)
                }
            }
        }

        fun doCommitCommand() {
            //ユーザ名とメールアドレスが未入力ならば入力する
            while (ConfigUserInfo.userName.isEmpty() || ConfigUserInfo.mailAddr.isEmpty()) {
                val dialog = UserNameDialog(ConfigUserInfo.userName, ConfigUserInfo.mailAddr)
                if (dialog.showDialog() != ButtonType.OK) {
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

}

