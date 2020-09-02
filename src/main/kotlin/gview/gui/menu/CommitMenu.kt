package gview.gui.menu

import gview.gui.framework.GviewCommonDialog
import gview.gui.framework.GviewMenuItem
import javafx.event.EventHandler
import javafx.scene.control.Menu
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination

class CommitMenu: Menu("コミット(_C)") {

    private val tagSearchMenu = GviewMenuItem(
            text= "タグを検索(_F)...",
            accelerator = KeyCodeCombination(KeyCode.F, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN)
    ) { onTagSearch() }

    private val commentSearchMenu = GviewMenuItem(
            text = "コメントを検索(_S)...",
            accelerator = KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN)
    ) { onCommentSearch() }

    private val branchMenu = GviewMenuItem(
            text = "ブランチ...",
            accelerator = KeyCodeCombination(KeyCode.B, KeyCombination.SHORTCUT_DOWN),
            iconLiteral = "mdi-source-branch"
    ) { onBranch() }

    private val mergeMenu = GviewMenuItem(
            text = "マージ...",
            accelerator = KeyCodeCombination(KeyCode.M, KeyCombination.SHORTCUT_DOWN),
            iconLiteral = "mdi-source-merge"
    ) { onMerge() }

    private val cherryPickMenu = GviewMenuItem(
            text = "チェリーピック...",
            accelerator = KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN),
            iconLiteral = "mdi-source-pull"
    ) { onCherryPick() }

    private val newTagMenu = GviewMenuItem(
            text = "タグを作成..."
    ) { onNewTag() }

    private val removeTagMenu = GviewMenuItem(
            text = "タグを削除..."
    ) { onRemoveTag() }

    init {
        items.setAll(
                tagSearchMenu,
                commentSearchMenu,
                SeparatorMenuItem(),
                branchMenu,
                mergeMenu,
                cherryPickMenu,
                SeparatorMenuItem(),
                newTagMenu,
                removeTagMenu
        )
        onShowing = EventHandler { onShowingMenu() }
    }

    private fun onShowingMenu() {
    }

    private fun onTagSearch() {
        val tagName = GviewCommonDialog.createSimpleTextDialog("タグ検索", "検索するタグ")
        if(tagName != null) {
            //タグ検索(tagName)
        }
    }

    private fun onCommentSearch() {
        val comment = GviewCommonDialog.createSimpleTextDialog("コメント検索", "検索する文字列")
        if(comment != null) {
            //コメント検索(comment)
        }
    }

    private fun onBranch() {
    }

    private fun onMerge() {
    }

    private fun onCherryPick() {
    }

    private fun onNewTag() {
    }

    private fun onRemoveTag() {
    }
}