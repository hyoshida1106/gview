package gview.view.menu

import gview.GvApplication
import gview.conf.SystemModal
import gview.model.GvRepository
import gview.resourceBundle
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
import org.jetbrains.annotations.NonNls

class FileMenu: Menu(resourceBundle().getString("FileMenu")) {

    @NonNls
    private val openMenu = GvMenuItem(
        text = resourceBundle().getString("FileOpenRepository"),
        accelerator = KeyCodeCombination(
            KeyCode.O,
            KeyCombination.SHORTCUT_DOWN
        ),
        iconLiteral = "mdi2f-folder"
    ) { onFileOpenRepo() }

    @NonNls
    private val createMenu = GvMenuItem(
        text = resourceBundle().getString("FileCreateRepository"),
        accelerator = KeyCodeCombination(
            KeyCode.N,
            KeyCombination.SHORTCUT_DOWN
        ),
        iconLiteral = "mdi2f-folder-plus"
    ) { onFileCreateRepo() }

    @NonNls
    private val cloneMenu = GvMenuItem(
        text = resourceBundle().getString("FileCloneRepository"),
        accelerator = KeyCodeCombination(
            KeyCode.C,
            KeyCombination.SHORTCUT_DOWN
        ),
        iconLiteral = "mdi2c-cloud-download"
    ) { onFileCloneRepo() }

    private val quitMenu = GvMenuItem(
        text = resourceBundle().getString("FileQUit")
    ) { onFileQuit() }

    //以前に開いたファイル用メニューのプレースホルダ
    private val lastFileMenuArray = listOf(
        GvMenuItem(text = "") { onLastFileMenu(it.source as MenuItem) },
        GvMenuItem(text = "") { onLastFileMenu(it.source as MenuItem) },
        GvMenuItem(text = "") { onLastFileMenu(it.source as MenuItem) },
        GvMenuItem(text = "") { onLastFileMenu(it.source as MenuItem) },
        GvMenuItem(text = "") { onLastFileMenu(it.source as MenuItem) }
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
        if (lastOpenedFiles.isEmpty()) {
            with(lastFileMenuArray[0]) {
                isVisible = true
                text = resourceBundle().getString("FileNoFile")
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
            GvRepository.open(filePath)
            SystemModal.addLastOpenedFile(filePath)
        }
    }

    //既存のリポジトリを開く
    private fun onFileOpenRepo() {
        val chooser = DirectoryChooser()
        chooser.title = resourceBundle().getString("FileOpenRepositoryPath")
        val dir = chooser.showDialog(MainWindow.root.scene.window) ?: return
        MainWindow.controller.runTask {
            val filePath = dir.absolutePath
            GvRepository.open(filePath)
            SystemModal.addLastOpenedFile(filePath)
        }
    }

    //新規作成
    private fun onFileCreateRepo() {
        val chooser = DirectoryChooser()
        chooser.title = resourceBundle().getString("FileCreateRepositoryPath")
        val dir = chooser.showDialog(MainWindow.root.scene.window) ?: return
        MainWindow.controller.runTask {
            val filePath = dir.absolutePath
            GvRepository.init(filePath)
            SystemModal.addLastOpenedFile(filePath)
        }
    }

    //クローン
    private fun onFileCloneRepo() {
        val dialog = CloneRepositoryDialog("", "")
        if (dialog.showDialog() == ButtonType.OK) {
            MainWindow.controller.runTask {
                GvRepository.clone(dialog.localPath, dialog.remotePath, dialog.bareRepo)
                SystemModal.addLastOpenedFile(dialog.localPath)
            }
        }
    }

    //プログラム終了
    private fun onFileQuit() {
        GvApplication.confirmToQuit()
    }

}
