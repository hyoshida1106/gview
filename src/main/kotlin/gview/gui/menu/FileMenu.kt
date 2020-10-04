package gview.gui.menu

import gview.gui.dialog.CloneRepositoryDialog
import gview.gui.dialog.ConfirmationDialog
import gview.gui.dialog.ConfirmationDialog.ConfirmationType
import gview.gui.framework.GviewMenuItem
import gview.gui.main.MainWindow
import gview.model.GviewRepositoryModel
import javafx.event.EventHandler
import javafx.scene.control.ButtonType
import javafx.scene.control.Menu
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.stage.DirectoryChooser
import kotlin.system.exitProcess

class FileMenu
    : Menu("ファイル(_F)") {

    private val openMenu = GviewMenuItem(
            text= "リポジトリを開く(_O)...",
            accelerator = KeyCodeCombination(
                    KeyCode.O,
                    KeyCombination.SHORTCUT_DOWN),
            iconLiteral = "mdi-folder"
    ) { onFileOpenRepo() }

    private val createMenu = GviewMenuItem(
            text = "新規リポジトリ(_N)...",
            accelerator = KeyCodeCombination(
                    KeyCode.N,
                    KeyCombination.SHORTCUT_DOWN),
            iconLiteral = "mdi-folder-plus"
    ) { onFileCreateRepo() }

    private val cloneMenu = GviewMenuItem(
            text = "クローン(_C)...",
            accelerator = KeyCodeCombination(
                    KeyCode.C,
                    KeyCombination.SHORTCUT_DOWN),
            iconLiteral = "mdi-cloud-download"
    ) { onFileCloneRepo() }

    private val quitMenu = GviewMenuItem(
            text = "終了(_X)"
    ) { onFileQuit() }

    init {
        items.setAll(
                openMenu,
                createMenu,
                cloneMenu,
                SeparatorMenuItem(),
                quitMenu
        )
        onShowing = EventHandler { onShowingMenu() }
    }

    //"File"メニュー表示
    private fun onShowingMenu() {
    }

    //既存のリポジトリを開く
    private fun onFileOpenRepo() {
        val chooser = DirectoryChooser()
        chooser.title = "オープンするリポジトリのパスを指定してください"
        val dir = chooser.showDialog(MainWindow.root.scene.window) ?: return
        MainWindow.controller.runTask {
            GviewRepositoryModel.currentRepository.openExist(
                    dir.absolutePath) }
    }

    //新規作成
    private fun onFileCreateRepo() {
        val chooser = DirectoryChooser()
        chooser.title = "リポジトリを生成するパスを指定してください"
        val dir = chooser.showDialog(MainWindow.root.scene.window) ?: return
        MainWindow.controller.runTask {
            GviewRepositoryModel.currentRepository.createNew(
                    dir.absolutePath) }
    }

    //クローン
    private fun onFileCloneRepo() {
        val dialog = CloneRepositoryDialog("", "")
        if(dialog.showDialog() == ButtonType.OK) {
            MainWindow.controller.runTask {
                GviewRepositoryModel.currentRepository.clone(
                        dialog.localPath,
                        dialog.remotePath,
                        dialog.bareRepo)
            }
        }
    }

    //プログラム終了
    private fun onFileQuit() {
        val message = "アプリケーションを終了しますか？"
        if(ConfirmationDialog(ConfirmationType.YesNo, message).showDialog()) {
            exitProcess(0)
        }
    }
}
