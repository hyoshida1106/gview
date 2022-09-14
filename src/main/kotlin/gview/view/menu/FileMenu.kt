package gview.view.menu

import gview.GvApplication
import gview.conf.SystemModal
import gview.resourceBundle
import gview.view.function.RepositoryFunction
import javafx.event.EventHandler
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination

class FileMenu: Menu(resourceBundle().getString("FileMenu.Title")) {

    /* リポジトリを開く(_O)... */
    private val openMenu = GvMenuItem(
        text = resourceBundle().getString("FileMenu.OpenRepository"),
        accelerator = KeyCodeCombination(
            KeyCode.O,
            KeyCombination.SHORTCUT_DOWN
        ),
        iconLiteral = "mdi2f-folder-open-outline"           // NON-NLS
    ) {
        RepositoryFunction.doOpen()
    }

    /* 新規リポジトリ(_N)... */
    private val createMenu = GvMenuItem(
        text = resourceBundle().getString("FileMenu.CreateNewRepository"),
        accelerator = KeyCodeCombination(
            KeyCode.N,
            KeyCombination.SHORTCUT_DOWN
        ),
        iconLiteral = "mdi2f-folder-plus-outline"           // NON-NLS
    ) {
        RepositoryFunction.doCreate()
    }

    /* クローン(_C)... */
    private val cloneMenu = GvMenuItem(
        text = resourceBundle().getString("FileMenu.CloneRepository"),
        accelerator = KeyCodeCombination(
            KeyCode.C,
            KeyCombination.SHORTCUT_DOWN
        ),
        iconLiteral = "mdi2f-folder-download-outline"       // NON-NLS
    ) {
        RepositoryFunction.doClone()
    }

    /* 終了(_X) */
    private val quitMenu = GvMenuItem(
        text = resourceBundle().getString("FileMenu.Quit"),
        iconLiteral = "mdi2s-stop-circle-outline"           // NON-NLS
    ) {
        GvApplication.confirmToQuit()
    }

    //以前に開いたファイル用メニューのプレースホルダ
    private val lastFileMenuArray = Array(5){
        GvMenuItem(text = "") { RepositoryFunction.doOpen((it.source as MenuItem).text) }
    }

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
}
