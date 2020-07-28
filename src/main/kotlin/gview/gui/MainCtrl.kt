package gview.gui

import gview.conf.Configuration
import gview.gui.branchlist.BranchListView
import gview.gui.commitlist.CommitListView
import gview.gui.framework.BaseCtrl
import gview.gui.util.CommonDialog
import gview.model.GviewRepositoryModel
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import kotlin.system.exitProcess


class MainCtrl : BaseCtrl() {

    @FXML private lateinit var mainSplit:  SplitPane
    @FXML private lateinit var branchList: AnchorPane
    @FXML private lateinit var commitList: AnchorPane
    @FXML private lateinit var commitInfo: AnchorPane

    //リポジトリ
    val repository = GviewRepositoryModel()

    //SplitPaneのDivider位置を保持するProperty
    private val splitPositionsProperty = SimpleObjectProperty<DoubleArray>()
    private val splitPositions: DoubleArray get() { return splitPositionsProperty.value }

    //初期化処理
    fun initialize() {
        //Dividerの初期設定
        splitPositionsProperty.value = Configuration.systemModal.mainSplitPosProperty.value
        mainSplit.setDividerPositions(splitPositions[0], splitPositions[1])

        //Divider移動時にsplitPositionPropertyを更新する
        mainSplit.dividers[0].positionProperty().addListener { _, _, value
            -> splitPositions[0] = value.toDouble() }
        mainSplit.dividers[1].positionProperty().addListener { _, _, value
            -> splitPositions[1] = value.toDouble() }

        //Configuration情報にsplitPositionsPropertyをbind
        Configuration.systemModal.mainSplitPosProperty.bind(splitPositionsProperty)

        //初期化
        branchList.children.add(BranchListView.root)
        commitList.children.add(CommitListView.root)

        initStatusBar()
        initMenuBar()
    }

    /* =================================================================
        ステータスバー
     */
    @FXML private lateinit var statusBar: AnchorPane
    @FXML private lateinit var repositoryPath: Label

    //初期化
    private fun initStatusBar() {
        //リポジトリのパスを表示
        repositoryPath.textProperty().bind(repository.localPathProperty)
    }

    /* =================================================================
        メニューバー
     */
    @FXML private lateinit var menu: MenuBar

    //初期化
    private fun initMenuBar() {
    }

    /* ================================================================
        File Menu
     */
    @FXML private lateinit var fileMenu: Menu
    @FXML private lateinit var fileOpenMenu: MenuItem
    @FXML private lateinit var fileCreateMenu: MenuItem
    @FXML private lateinit var fileQuitMenu: MenuItem

    //"File"メニュー表示
    @FXML private fun onShowingFileMenu() {
    }

    //既存リポジトリを開く
    @FXML private fun onMenuOpenRepository() {
        val chooser = DirectoryChooser()
        chooser.title = "リポジトリを開く"
        val dir = chooser.showDialog(MainView.root.scene.window as? Stage?)
        if(dir != null) {
            try {
                repository.openExist(dir.path)
            } catch(e: Exception) {
                CommonDialog.createErrorDialog(e)
            }
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