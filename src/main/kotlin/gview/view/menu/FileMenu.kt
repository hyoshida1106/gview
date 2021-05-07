package gview.view.menu

import gview.GvApplication
import gview.conf.SystemModal
import gview.view.dialog.CloneRepositoryDialog
import gview.view.main.MainWindow
import javafx.event.EventHandler
import javafx.scene.control.ButtonType
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.stage.DirectoryChooser

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

    //以前に開いたファイル用メニューのプレースホルダ
    private val lastFileMenuArray = listOf(
        GviewMenuItem(text = "") { onLastFileMenu(it.source as MenuItem) },
        GviewMenuItem(text = "") { onLastFileMenu(it.source as MenuItem) },
        GviewMenuItem(text = "") { onLastFileMenu(it.source as MenuItem) },
        GviewMenuItem(text = "") { onLastFileMenu(it.source as MenuItem) },
        GviewMenuItem(text = "") { onLastFileMenu(it.source as MenuItem) }
    )

    init {
        items.setAll(
            openMenu,
            createMenu,
            cloneMenu,
            SeparatorMenuItem()
        )
        items.addAll(
            lastFileMenuArray
        )
        items.addAll(
            SeparatorMenuItem(),
            quitMenu
        )
        onShowing = EventHandler { onShowingMenu() }
    }

    //"File"メニュー表示
    private fun onShowingMenu() {
        //ファイルメニュー非表示で初期化
        lastFileMenuArray.forEach {
            it.isVisible = false
            it.isDisable = true
            it.text = ""
        }
        val lastOpenedFiles = SystemModal.lastOpenedFiles
        if(lastOpenedFiles.isEmpty()) {
            with(lastFileMenuArray[0]) {
                isVisible = true
                text = "ファイルなし"
            }
        } else {
            val item = lastFileMenuArray.iterator()
            lastOpenedFiles.forEach {
                with(item.next()) {
                    isVisible = true
                    isDisable = false
                    text = it
                }
            }
        }
    }

    //以前に開いたファイル選択時の処理
    private fun onLastFileMenu(item: MenuItem) {
        MainWindow.controller.runTask {
            val filePath = item.text
            GvApplication.instance.currentRepository.openExist(filePath)
            SystemModal.addLastOpenedFile(filePath)
        }
    }

    //既存のリポジトリを開く
    private fun onFileOpenRepo() {
        val chooser = DirectoryChooser()
        chooser.title = "オープンするリポジトリのパスを指定してください"
        val dir = chooser.showDialog(MainWindow.root.scene.window) ?: return
        MainWindow.controller.runTask {
            val filePath = dir.absolutePath
            GvApplication.instance.currentRepository.openExist(filePath)
            SystemModal.addLastOpenedFile(filePath)
        }
    }

    //新規作成
    private fun onFileCreateRepo() {
        val chooser = DirectoryChooser()
        chooser.title = "リポジトリを生成するパスを指定してください"
        val dir = chooser.showDialog(MainWindow.root.scene.window) ?: return
        MainWindow.controller.runTask {
            val filePath = dir.absolutePath
            GvApplication.instance.currentRepository.createNew(filePath)
            SystemModal.addLastOpenedFile(filePath)
        }
    }

    //クローン
    private fun onFileCloneRepo() {
        val dialog = CloneRepositoryDialog("", "")
        if(dialog.showDialog() == ButtonType.OK) {
            MainWindow.controller.runTask {
                GvApplication.instance.currentRepository.clone(dialog.localPath, dialog.remotePath, dialog.bareRepo)
                SystemModal.addLastOpenedFile(dialog.localPath)
            }
        }
    }

    //プログラム終了
    private fun onFileQuit() {
        GvApplication.instance.confirmToQuit()
    }
}
