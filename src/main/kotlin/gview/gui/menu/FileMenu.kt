package gview.gui.menu

import gview.gui.framework.GviewCommonDialog
import gview.gui.framework.GviewMenuItem
import gview.gui.main.MainWindow
import gview.model.GviewRepositoryModel
import javafx.event.EventHandler
import javafx.scene.control.Menu
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import kotlin.system.exitProcess

class FileMenu: Menu("ファイル(_F)") {

    private val openMenu = GviewMenuItem(
            text= "リポジトリを開く(_O)...",
            accelerator = KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN),
            iconLiteral = "mdi-folder"
    ) { onOpenRepository() }

    private val createMenu = GviewMenuItem(
            text = "新規リポジトリ(_N)...",
            accelerator = KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN),
            iconLiteral = "mdi-folder-plus"
    ) { onCreateRepository() }

    private val quitMenu = GviewMenuItem(
            text = "終了(_X)"
    ) { onQuit() }

    init {
        items.setAll(
                openMenu,
                createMenu,
                SeparatorMenuItem(),
                quitMenu
        )
        onShowing = EventHandler { onShowingMenu() }
    }

    //"File"メニュー表示
    private fun onShowingMenu() {
    }

    //既存リポジトリを開く
    private fun onOpenRepository() {
        val chooser = DirectoryChooser()
        chooser.title = "リポジトリを開く"
        val dir = chooser.showDialog(MainWindow.root.scene.window as? Stage?)
        if(dir != null) {
            try {
                GviewRepositoryModel.currentRepository.openExist(dir.path)
            } catch(e: Exception) {
                GviewCommonDialog.errorDialog(e)
            }
        }
    }

    //リポジトリ新規作成
    private fun onCreateRepository() {
        val chooser = DirectoryChooser()
        chooser.title = "リポジトリ新規作成"
        val dir = chooser.showDialog(MainWindow.root.scene.window as? Stage?)
        if(dir != null) {
            try {
                GviewRepositoryModel.currentRepository.createNew(dir.absolutePath)
            } catch(e: Exception) {
                GviewCommonDialog.errorDialog(e)
            }
        }
    }

    //終了確認
    private fun onQuit() {
        if(GviewCommonDialog.confirmationDialog("アプリケーションを終了しますか？")) {
            exitProcess(0)
        }
    }

}
