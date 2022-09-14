package gview.view.menu

import gview.resourceBundle
import javafx.event.EventHandler
import javafx.scene.control.Menu
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination

class CommitMenu: Menu(resourceBundle().getString("CommitMenu.Title")) {

    private val checkOutMenuItem = GvMenuItem(
        text = resourceBundle().getString("CommitMenu.Checkout"),
        iconLiteral = "mdi2s-source-branch",            // NON-NLS
        bold = true
    ) { }

    private val branchMenuItem = GvMenuItem(
        text = resourceBundle().getString("CommitMenu.Branch"),
        accelerator = KeyCodeCombination(
            KeyCode.B,
            KeyCombination.SHORTCUT_DOWN),
        iconLiteral = "mdi2s-source-branch"            // NON-NLS
    ) { }

    private val mergeMenuItem = GvMenuItem(
        text = resourceBundle().getString("CommitMenu.Merge"),
        accelerator = KeyCodeCombination(
            KeyCode.M,
            KeyCombination.SHORTCUT_DOWN),
        iconLiteral = "mdi2s-source-merge"            // NON-NLS
    ) { }

    private val tagMenuItem = GvMenuItem(
        text = resourceBundle().getString("CommitMenu.Tag"),
        iconLiteral = "mdi2t-tag-outline"            // NON-NLS
    ) { }

    /* リベース/インタラクティブなリベース... */
    private val rebaseMenuItem = GvMenuItem(
        text = resourceBundle().getString("CommitMenu.Rebase"),
        iconLiteral = "mdi2s-source-merge"           // NON-NLS
    ) { }

    private val tagSearchMenuItem = GvMenuItem(
        text = resourceBundle().getString("CommitMenu.TagSearch")
    ) { searchCommitByTagName() }

    private val commentSearchMenuItem = GvMenuItem(
        text = resourceBundle().getString("CommitMenu.CommentSearch")
    ) { searchCommitByComment() }

    init {
        items.setAll(
            checkOutMenuItem,
            branchMenuItem,
            mergeMenuItem,
            tagMenuItem,
            rebaseMenuItem,
            SeparatorMenuItem(),
            tagSearchMenuItem,
            commentSearchMenuItem
        )
        onShowing = EventHandler { onShowingMenu() }
    }

    private fun onShowingMenu() {
        checkOutMenuItem.isDisable = true
        mergeMenuItem.isDisable = true
        mergeMenuItem.isDisable = true
        tagMenuItem.isDisable = true
        rebaseMenuItem.isDisable = true
        tagSearchMenuItem.isDisable = true
        commentSearchMenuItem.isDisable = true
    }

    private fun searchCommitByTagName() {
    }

    private fun searchCommitByComment() {
    }
}