package gview.view.function

import gview.conf.GitConfigInfo
import gview.model.GvRepository
import gview.resourceBundle
import gview.view.dialog.*
import javafx.scene.control.ButtonType

object WorkTreeFunction {

    val canStage get() = GvRepository.currentRepository?.workFiles?.changedFiles?.value?.isNotEmpty() ?: false

    fun doStage() {
        val dialog = SelectChangedFilesDialog(resourceBundle().getString("SelectChangedFiles.Stage"))
        if (dialog.showDialog() == ButtonType.OK) {
            try {
                GvRepository.currentRepository?.workFiles?.stageFiles(dialog.selectedFiles)
            } catch (e: Exception) {
                ErrorDialog(e).showDialog()
            }
        }
    }

    val canUnStage get() = GvRepository.currentRepository?.workFiles?.stagedFiles?.value?.isNotEmpty() ?: false

    fun doUnStage() {
        val dialog = SelectUnStageFilesDialog()
        if (dialog.showDialog() == ButtonType.OK) {
            try {
                GvRepository.currentRepository?.workFiles?.unStageFiles(dialog.selectedFiles)
            } catch (e: Exception) {
                ErrorDialog(e).showDialog()
            }
        }
    }

    val canCommit get() = canUnStage

    fun doCommit() {
        //ユーザ名とメールアドレスが未入力ならば入力する
        var userName = GitConfigInfo.userName
        var mailAddress = GitConfigInfo.mailAddress
        while (userName.isEmpty() || mailAddress.isEmpty()) {
            val dialog = UserNameDialog(userName, mailAddress)
            if (dialog.showDialog() != ButtonType.OK) return
            userName = dialog.userName
            mailAddress = dialog.mailAddr
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
                mailAddress)
        } catch (e: Exception) {
            ErrorDialog(e).showDialog()
        }
    }

    val canDiscard get() = canStage

    fun doDiscard() {
        val dialog = SelectChangedFilesDialog(resourceBundle().getString("SelectChangedFiles.Discard"))
        if (dialog.showDialog() == ButtonType.OK) {
            try {
                GvRepository.currentRepository?.workFiles?.discardFiles(dialog.selectedFiles)
            } catch (e: Exception) {
                ErrorDialog(e).showDialog()
            }
        }

    }
}