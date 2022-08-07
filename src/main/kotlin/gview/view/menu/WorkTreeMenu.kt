package gview.view.menu

import gview.model.GvRepository
import gview.resourceBundle
import gview.view.function.WorkTreeFunction
import javafx.event.EventHandler
import javafx.scene.control.Menu
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import org.jetbrains.annotations.NonNls

class WorkTreeMenu: Menu(resourceBundle().getString("WorkTreeMenu.Title")) {

    /* コミット(_C)... */
    @NonNls
    private val commitMenuItem = GvMenuItem(
        text = resourceBundle().getString("WorkTreeMenu.Commit"),
        accelerator = KeyCodeCombination(
            KeyCode.C,
            KeyCombination.SHORTCUT_DOWN
        ),
        iconLiteral = "mdi2s-source-commit"
    ) { WorkTreeFunction.doCommit() }

    /* インデクスに追加(_S)... */
    @NonNls
    private val stageMenuItem = GvMenuItem(
        text = resourceBundle().getString("WorkTreeMenu.Stage"),
        accelerator = KeyCodeCombination(
            KeyCode.S,
            KeyCombination.SHORTCUT_DOWN
        ),
        iconLiteral = "mdi2a-arrow-up-bold-circle-outline"
    ) { WorkTreeFunction.doStage() }

    /* インデクスから除く(_U)... */
    @NonNls
    private val unStageMenuItem = GvMenuItem(
        text = resourceBundle().getString("WorkTreeMenu.UnStage"),
        accelerator = KeyCodeCombination(
            KeyCode.U,
            KeyCombination.SHORTCUT_DOWN,
            KeyCombination.SHIFT_DOWN
        ),
        iconLiteral = "mdi2a-arrow-down-bold-circle-outline"
    ) { WorkTreeFunction.doUnStage() }

    /* 変更の破棄(_X)... */
    @NonNls
    private val discardMenuItem = GvMenuItem(
        text = resourceBundle().getString("WorkTreeMenu.DiscardChanges"),
        accelerator = KeyCodeCombination(
            KeyCode.X,
            KeyCombination.SHORTCUT_DOWN,
            KeyCombination.SHIFT_DOWN
        ),
        iconLiteral = "mdi2r-restore"
    ) { WorkTreeFunction.doDiscard() }

    init {
        items.setAll(
            commitMenuItem,
            stageMenuItem,
            unStageMenuItem,
            discardMenuItem
        )
        onShowing = EventHandler { onShowingMenu() }
    }

    private fun onShowingMenu() {
        commitMenuItem.isDisable = !WorkTreeFunction.canCommit
        stageMenuItem.isDisable = !WorkTreeFunction.canStage
        unStageMenuItem.isDisable = !WorkTreeFunction.canUnStage
        discardMenuItem.isDisable = !WorkTreeFunction.canDiscard
    }

}

