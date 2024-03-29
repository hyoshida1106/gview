package gview.view.menu

import gview.resourceBundle
import gview.view.function.BranchFunction
import javafx.event.EventHandler
import javafx.scene.control.Menu
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination

class BranchMenu : Menu(resourceBundle().getString("BranchMenu.Title")) {

    /* プル */
    private val pullMenu = GvMenuItem(
        text = resourceBundle().getString("BranchMenu.Pull"),
        accelerator = KeyCodeCombination(
            KeyCode.L,
            KeyCombination.SHORTCUT_DOWN,
            KeyCombination.SHIFT_DOWN
        ),
        iconLiteral = "mdi2f-folder-download"               // NON-NLS
    ) { BranchFunction.doPull() }

    /* プッシュ */
    private val pushMenu = GvMenuItem(
        text = resourceBundle().getString("BranchMenu.Push"),
        accelerator = KeyCodeCombination(
            KeyCode.P,
            KeyCombination.SHORTCUT_DOWN,
            KeyCombination.SHIFT_DOWN
        ),
        iconLiteral = "mdi2s-source-branch-sync"          // NON-NLS
    ) { BranchFunction.doPush() }

    init {
        items.setAll(
            pullMenu,
            pushMenu
        )
        onShowing = EventHandler { onShowingMenu() }
    }

    /* メニュー表示 */
    private fun onShowingMenu() {
        pushMenu.isDisable = BranchFunction.canPush().not()
        pullMenu.isDisable = BranchFunction.canPull().not()
    }
}