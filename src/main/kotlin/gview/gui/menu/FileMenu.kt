package gview.gui.menu

import gview.gui.dialog.CloneRepositoryDialog
import gview.gui.framework.GviewCommonDialog
import gview.gui.framework.GviewMenuItem
import gview.gui.main.MainWindow
import gview.model.GviewRepositoryModel
import javafx.concurrent.Task
import javafx.event.EventHandler
import javafx.scene.Cursor
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
    ) { doOpenRepository() }

    private val createMenu = GviewMenuItem(
            text = "新規リポジトリ(_N)...",
            accelerator = KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN),
            iconLiteral = "mdi-folder-plus"
    ) { doCreateRepository() }

    private val cloneMenu = GviewMenuItem(
            text = "クローン(_C)...",
            accelerator = KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN),
            iconLiteral = "mdi-cloud-download"
    ) { doCloneRepository() }

    private val quitMenu = GviewMenuItem(
            text = "終了(_X)"
    ) { doCheckQuit() }

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

    companion object {

        //既存リポジトリを開く
        fun doOpenRepository() {
            val scene = MainWindow.root.scene
            val chooser = DirectoryChooser()
            chooser.title = "リポジトリを開く"
            val dir = chooser.showDialog(scene.window as? Stage?)
            if (dir != null) {
                try {
                    val task = object : Task<Int>() {
                        override fun call(): Int {
                            GviewRepositoryModel.currentRepository.openExist(dir.path)
                            return 0
                        }
                    }
                    scene.cursor = Cursor.WAIT
                    GviewRepositoryModel.currentRepository.openExist(dir.path)
                    scene.cursor = Cursor.DEFAULT
                } catch (e: Exception) {
                    GviewCommonDialog.errorDialog(e)
                }
            }
        }

        //リポジトリ新規作成
        fun doCreateRepository() {
            val chooser = DirectoryChooser()
            chooser.title = "リポジトリ新規作成"
            val dir = chooser.showDialog(MainWindow.root.scene.window as? Stage?)
            if (dir != null) {
                try {
                    GviewRepositoryModel.currentRepository.createNew(dir.absolutePath)
                } catch (e: Exception) {
                    GviewCommonDialog.errorDialog(e)
                }
            }
        }

        fun doCloneRepository() {
            val dialog = CloneRepositoryDialog("", "")
            dialog.showDialog()
        }

        //終了確認
        fun doCheckQuit() {
            if(GviewCommonDialog.confirmationDialog("アプリケーションを終了しますか？")) {
                exitProcess(0)
            }
        }

    }

}
