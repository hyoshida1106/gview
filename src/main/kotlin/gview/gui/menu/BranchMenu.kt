package gview.gui.menu

import gview.gui.framework.GviewMenuItem
import javafx.event.EventHandler
import javafx.scene.control.Menu
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination

class BranchMenu: Menu("ブランチ(_B)") {

    private val checkoutMenu = GviewMenuItem(
            text= "チェックアウト(_C)",
            iconLiteral = "mdi-folder-star"
    ) { onCheckOut() }

    private val pushMenu = GviewMenuItem(
            text= "プッシュ(_P)",
            accelerator = KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN),
            iconLiteral = "mdi-folder-upload"
    ) { onPush() }

    private val pullMenu = GviewMenuItem(
            text = "プル(_L)",
            accelerator = KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN),
            iconLiteral = "mdi-folder-download"
    ) { onPull() }

    private val renameMenu = GviewMenuItem(
            text = "名称変更(_R)...",
            iconLiteral = "mdi-folder-move"
    ) { onRename() }

    private val removeMenu = GviewMenuItem(
            text = "削除(_D)",
            iconLiteral = "mdi-folder-remove"
    ) { onRemove() }

    init {
        items.setAll(
                checkoutMenu,
                pushMenu,
                pullMenu,
                SeparatorMenuItem(),
                renameMenu,
                removeMenu
        )
        onShowing = EventHandler { onShowingMenu() }
    }

    //メニュー表示
    private fun onShowingMenu() {
    }

    private fun onCheckOut() {
    }

    private fun onPush() {
    }

    private fun onPull() {
    }

    private fun onRename() {
    }

    private fun onRemove() {
    }
}

