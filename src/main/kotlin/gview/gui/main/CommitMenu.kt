package gview.gui.main

import gview.gui.framework.GviewBaseMenu
import gview.gui.framework.GviewCommonDialog
import javafx.fxml.FXML
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem

object CommitMenu: GviewBaseMenu<CommitMenuCtrl>("/view/CommitMenu.fxml")

class CommitMenuCtrl {

    @FXML private lateinit var commitMenu: Menu
    @FXML private lateinit var commitTagSearchMenu: MenuItem
    @FXML private lateinit var commitCommentSearchMenu: MenuItem
    @FXML private lateinit var commitCreateBranchMenu: MenuItem
    @FXML private lateinit var commitMergeMenu: MenuItem
    @FXML private lateinit var commitCherryPickMenu: MenuItem
    @FXML private lateinit var commitCreateTagMenu: MenuItem
    @FXML private lateinit var commitRemoveTagMenu: MenuItem

    @FXML private fun onShowingMenu() {
        commitCreateBranchMenu.isDisable = true
        commitMergeMenu.isDisable = true
        commitCherryPickMenu.isDisable = true
        commitCreateTagMenu.isDisable = true
        commitRemoveTagMenu.isDisable = true
    }

    @FXML private fun onCommitTagSearch() {
        val tagName = GviewCommonDialog.createSimpleTextDialog("タグ検索", "検索するタグ")
        if(tagName != null) {
            //タグ検索(tagName)
        }
    }

    @FXML private fun onCommitCommentSearch() {
        val comment = GviewCommonDialog.createSimpleTextDialog("コメント検索", "検索する文字列")
        if(comment != null) {
            //コメント検索(comment)
        }
    }

}