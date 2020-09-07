package gview.gui.menu

import gview.gui.dialog.CloneRepositoryDialog
import gview.gui.dialog.CreateRepositoryDialog
import gview.gui.dialog.OpenRepositoryDialog
import gview.gui.framework.GviewCommonDialog
import gview.gui.framework.GviewMenuItem
import javafx.event.EventHandler
import javafx.scene.control.Menu
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import kotlin.system.exitProcess

class FileMenu: Menu("ファイル(_F)") {

    private val openMenu = GviewMenuItem(
            text= "リポジトリを開く(_O)...",
            accelerator = KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN),
            iconLiteral = "mdi-folder"
    ) {
        OpenRepositoryDialog("").showDialog()
    }

    private val createMenu = GviewMenuItem(
            text = "新規リポジトリ(_N)...",
            accelerator = KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN),
            iconLiteral = "mdi-folder-plus"
    ) {
        CreateRepositoryDialog("").showDialog()
    }

    private val cloneMenu = GviewMenuItem(
            text = "クローン(_C)...",
            accelerator = KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN),
            iconLiteral = "mdi-cloud-download"
    ) {
        CloneRepositoryDialog("", "").showDialog()
    }

    private val quitMenu = GviewMenuItem(
            text = "終了(_X)"
    ) {
        if (GviewCommonDialog.confirmationDialog("アプリケーションを終了しますか？")) {
            exitProcess(0)
        }
    }

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

}
