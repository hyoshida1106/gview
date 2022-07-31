package gview.view.menu

import gview.resourceBundle
import javafx.event.EventHandler
import javafx.scene.control.Menu
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import org.jetbrains.annotations.NonNls

class BranchMenu : Menu(resourceBundle().getString("BranchMenu.Title")) {

    /* プル */
    @NonNls
    private val pullMenu = GvMenuItem(
        text = resourceBundle().getString("BranchMenu.Pull"),
        accelerator = KeyCodeCombination(
            KeyCode.L,
            KeyCombination.SHORTCUT_DOWN,
            KeyCombination.SHIFT_DOWN
        ),
        iconLiteral = "mdi2f-folder-download"
    ) { }

    /* プッシュ */
    @NonNls
    private val pushMenu = GvMenuItem(
        text = resourceBundle().getString("BranchMenu.Push"),
        accelerator = KeyCodeCombination(
            KeyCode.P,
            KeyCombination.SHORTCUT_DOWN,
            KeyCombination.SHIFT_DOWN
        ),
        iconLiteral = "mdi2s-source-branch-sync"
    ) { }

    @NonNls
    private val fetchMenu = GvMenuItem(
        text = resourceBundle().getString("BranchMenu.Fetch"),
        iconLiteral = "mdi2s-source-branch-refresh"
    ) { }

    init {
        items.setAll(
            pullMenu,
            pushMenu,
            fetchMenu
        )
        onShowing = EventHandler { onShowingMenu() }
    }

    //メニュー表示
    private fun onShowingMenu() {
        pushMenu.isDisable = true
        pullMenu.isDisable = true
        fetchMenu.isDisable = true
    }

}