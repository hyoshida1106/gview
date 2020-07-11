package gview.gui

import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import kotlin.system.exitProcess
import gview.gui.framework.BaseCtrl
import gview.gui.util.CommonDialog


class MainCtrl : BaseCtrl() {

    @FXML private lateinit var mainSplit:  SplitPane
    @FXML private lateinit var branchTree: AnchorPane
    @FXML private lateinit var commitList: AnchorPane
    @FXML private lateinit var commitInfo: AnchorPane

    //初期化処理
    fun initialize() {
    }

    /* =================================================================
        Status Bar Instance
     */
    @FXML private lateinit var statusBar: AnchorPane
    @FXML private lateinit var repositoryPath: Label

    /* =================================================================
        Menu Bar Instance
     */
    @FXML private lateinit var menu: MenuBar

    /* ================================================================
        File Menu
     */
    @FXML private lateinit var fileMenu: Menu
    @FXML private lateinit var fileOpenMenu: MenuItem
    @FXML private lateinit var fileCreateMenu: MenuItem
    @FXML private lateinit var fileQuitMenu: MenuItem

    @FXML private fun onShowingFileMenu() {
    }

    @FXML private fun onMenuOpenRepository() {
        val chooser = DirectoryChooser()
        chooser.title = "リポジトリを開く"
        val dir = chooser.showDialog(MainView.root.scene.window as? Stage?)
        if(dir != null) {
            //Open
        }
    }

    @FXML
    private fun onMenuCreateNewRepository() {
        val chooser = DirectoryChooser()
        chooser.title = "リポジトリ新規作成"
        val dir = chooser.showDialog(MainView.root.scene.window as? Stage?)
        if(dir != null) {
            // Create
        }
    }

    @FXML
    private fun onMenuQuit() {
        if(CommonDialog.createConfirmDialog("アプリケーションを終了しますか？")) {
            exitProcess(0)
        }
    }

    /* ================================================================
        Branch Menu
     */
    @FXML private lateinit var branchMenu: Menu
    @FXML private lateinit var branchCheckoutMenu: MenuItem
    @FXML private lateinit var branchPushMenu: MenuItem
    @FXML private lateinit var branchPullMenu: MenuItem
    @FXML private lateinit var branchRenameMenu: MenuItem
    @FXML private lateinit var branchRemoveMenu: MenuItem

    @FXML
    private fun onShowingBranchMenu() {
        branchCheckoutMenu.isDisable = true
        branchPushMenu.isDisable = true
        branchPullMenu.isDisable = true
        branchRenameMenu.isDisable = true
        branchRemoveMenu.isDisable = true
    }

    /* ================================================================
        WorkTree Menu
     */
    @FXML private lateinit var workTreeMenu: Menu
    @FXML private lateinit var workTreeStageMenu: MenuItem
    @FXML private lateinit var workTreeUnstageMenu: MenuItem
    @FXML private lateinit var workTreeCommitMenu: MenuItem

    @FXML
    private fun onShowingWorkTreeMenu() {
        workTreeStageMenu.isDisable = true
        workTreeUnstageMenu.isDisable = true
        workTreeCommitMenu.isDisable = true
    }

    /* ================================================================
        Commit Menu
     */
    @FXML private lateinit var commitMenu: Menu
    @FXML private lateinit var commitTagSearchMenu: MenuItem
    @FXML private lateinit var commitCommentSearchMenu: MenuItem
    @FXML private lateinit var commitCreateBranchMenu: MenuItem
    @FXML private lateinit var commitMergeMenu: MenuItem
    @FXML private lateinit var commitCherryPickMenu: MenuItem
    @FXML private lateinit var commitCreateTagMenu: MenuItem
    @FXML private lateinit var commitRemoveTagMenu: MenuItem

    @FXML
    private fun onShowingCommitMenu() {
        commitCreateBranchMenu.isDisable = true
        commitMergeMenu.isDisable = true
        commitCherryPickMenu.isDisable = true
        commitCreateTagMenu.isDisable = true
        commitRemoveTagMenu.isDisable = true
    }

    @FXML
    private fun onCommitTagSearch() {
        val tagName = CommonDialog.createSimpleTextDialog("タグ検索", "検索するタグ")
        if(tagName != null) {
            //タグ検索(tagName)
        }
    }

    @FXML
    private fun onCommitCommentSearch() {
        val comment = CommonDialog.createSimpleTextDialog("コメント検索", "検索する文字列")
        if(comment != null) {
            //コメント検索(comment)
        }
    }

    /* ================================================================
        Help Menu
     */
    @FXML private lateinit var helpMenu: Menu
    @FXML private lateinit var helpHelpMenu: MenuItem
    @FXML private lateinit var helpAboutMenu: MenuItem

    @FXML
    private fun onShowingHelpMenu() {
        helpHelpMenu.isDisable = true
        helpAboutMenu.isDisable = true
    }

}