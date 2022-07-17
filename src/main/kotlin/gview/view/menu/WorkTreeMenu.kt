package gview.view.menu

import gview.conf.GitConfigInfo
import gview.model.GvRepository
import gview.view.dialog.*
import javafx.event.EventHandler
import javafx.scene.control.ButtonType
import javafx.scene.control.Menu
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination

class WorkTreeMenu: Menu("ワークツリー(_W)") {

    private val stageMenu = GvMenuItem(
            text = "ステージ(_S)...",
            accelerator = KeyCodeCombination(
                    KeyCode.S,
                    KeyCombination.SHORTCUT_DOWN),
            iconLiteral = "mdi-arrow-up-bold-circle-outline"
    ) { doStageCommand() }

    private val unstageMenu = GvMenuItem(
            text = "アンステージ(_U)...",
            accelerator = KeyCodeCombination(
                    KeyCode.S,
                    KeyCombination.SHORTCUT_DOWN,
                    KeyCombination.SHIFT_DOWN),
            iconLiteral = "mdi-arrow-down-bold-circle-outline"
    ) { doUnStageCommand() }

    private val commitMenu = GvMenuItem(
            text = "コミット(_C)...",
            accelerator = KeyCodeCombination(
                    KeyCode.C,
                    KeyCombination.SHORTCUT_DOWN),
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
        val headerData = GvRepository.currentRepository?.workFiles
        val stagedFileNumber = headerData?.stagedFiles?.value?.size ?: 0
        val changedFileNumber = headerData?.changedFiles?.value?.size ?: 0
        stageMenu.isDisable = changedFileNumber == 0
        unstageMenu.isDisable = stagedFileNumber == 0
        commitMenu.isDisable = stagedFileNumber == 0
    }

    companion object {

        fun doStageCommand() {
            val dialog = SelectStageFilesDialog()
            if (dialog.showDialog() == ButtonType.OK) {
                try {
                    GvRepository.currentRepository?.workFiles?.stageSelectedFiles(dialog.selectedFiles)
                } catch (e: Exception) {
                    ErrorDialog(e).showDialog()
                }
            }
        }

        fun doUnStageCommand() {
            val dialog = SelectUnStageFilesDialog()
            if (dialog.showDialog() == ButtonType.OK) {
                try {
                    GvRepository.currentRepository?.workFiles?.unStageFiles(dialog.selectedFiles)
                } catch (e: Exception) {
                    ErrorDialog(e).showDialog()
                }
            }
        }

        fun doCommitCommand() {
            //ユーザ名とメールアドレスが未入力ならば入力する
            var userName = GitConfigInfo.userName
            var mailAddr = GitConfigInfo.mailAddress
            while (userName.isEmpty() || mailAddr.isEmpty()) {
                val dialog = UserNameDialog(userName, mailAddr)
                if (dialog.showDialog() != ButtonType.OK) return
                userName = dialog.userName
                mailAddr = dialog.mailAddr
            }
            //対象ファイルを選択する
            val dialog = SelectCommitFilesDialog()
            if (dialog.showDialog() != ButtonType.OK) return
            //コミットを実行する
            try {
                GvRepository.currentRepository?.workFiles?.commitFiles(
                        dialog.selectedFiles,
                        dialog.message,
                        userName,
                        mailAddr)
            } catch (e: Exception) {
                ErrorDialog(e).showDialog()
            }
        }
    }

}

