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

class FileMenu: Menu(resourceBundle().getString("FileMenu.Title")) {

    /* リポジトリを開く(_O)... */
    @NonNls
    private val openMenu = GvMenuItem(
        text = resourceBundle().getString("FileMenu.OpenRepository"),
        accelerator = KeyCodeCombination(
            KeyCode.O,
            KeyCombination.SHORTCUT_DOWN
        ),
        iconLiteral = "mdi2f-folder-open-outline"
    ) { openRepository() }

    /* 新規リポジトリ(_N)... */
    @NonNls
    private val createMenu = GvMenuItem(
        text = resourceBundle().getString("FileMenu.CreateNewRepository"),
        accelerator = KeyCodeCombination(
            KeyCode.N,
            KeyCombination.SHORTCUT_DOWN
        ),
        iconLiteral = "mdi2f-folder-plus-outline"
    ) { createNewRepository() }

    /* クローン(_C)... */
    @NonNls
    private val cloneMenu = GvMenuItem(
        text = resourceBundle().getString("FileMenu.CloneRepository"),
        accelerator = KeyCodeCombination(
            KeyCode.C,
            KeyCombination.SHORTCUT_DOWN
        ),
        iconLiteral = "mdi2f-folder-download-outline"
    ) { cloneRepository() }

    /* 終了(_X) */
    @NonNls
    private val quitMenu = GvMenuItem(
        text = resourceBundle().getString("FileMenu.Quit"),
        iconLiteral = "mdi2s-stop-circle-outline"
    ) { quitApplication() }

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
                text = resourceBundle().getString("FileMenu.NoFileMessage")
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
        MainWindow.runTask {
            val filePath = item.text
            GvRepository.open(filePath)
            SystemModal.addLastOpenedFile(filePath)
        }
    }

    //既存のリポジトリを開く
    private fun openRepository() {
        val chooser = DirectoryChooser()
        chooser.title = resourceBundle().getString("FileMenu.OpenRepositoryPathMessage")
        val dir = chooser.showDialog(MainWindow.root.scene.window) ?: return
        MainWindow.runTask {
            val filePath = dir.absolutePath
            GvRepository.open(filePath)
            SystemModal.addLastOpenedFile(filePath)
        }
    }

    //新規作成
    private fun createNewRepository() {
        val chooser = DirectoryChooser()
        chooser.title = resourceBundle().getString("FileMenu.CreateNewRepositoryPathMessage")
        val dir = chooser.showDialog(MainWindow.root.scene.window) ?: return
        MainWindow.runTask {
            val filePath = dir.absolutePath
            GvRepository.init(filePath)
            SystemModal.addLastOpenedFile(filePath)
        }
    }

    //クローン
    private fun cloneRepository() {
        val dialog = CloneRepositoryDialog("", "")
        if (dialog.showDialog() == ButtonType.OK) {
            MainWindow.runTask {
                GvRepository.clone(dialog.localPath, dialog.remotePath, dialog.bareRepo)
                SystemModal.addLastOpenedFile(dialog.localPath)
            }
        }
    }

    //プログラム終了
    private fun quitApplication() {
        GvApplication.confirmToQuit()
    }

}
