package gview.gui.branchlist

import gview.gui.framework.GviewBasePaneCtrl
import gview.model.GviewRepositoryModel
import gview.model.branch.GviewBranchModel
import gview.model.branch.GviewLocalBranchModel
import gview.model.branch.GviewRemoteBranchModel
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*

class BranchListCtrl: GviewBasePaneCtrl() {

    //ブランチ一覧Tree
    @FXML private lateinit var branchTree: TreeView<GviewBranchModel>

    //ローカルブランチTree
    private var localTreeRoot = RootItem("Local")

    //リモートブランチTree
    private var remoteTreeRoot = RootItem("Remote")

    //View初期化
    fun initialize() {
        //Root Treeを作成
        val root = RootItem("Branch Root" )
        root.children.setAll(localTreeRoot, remoteTreeRoot)
        //ブランチTreeの初期設定
        branchTree.root = root
        branchTree.isShowRoot = false
        branchTree.setCellFactory { BranchTreeCell() }
        branchTree.selectionModel.clearSelection()
        branchTree.style = CSS.treeStyle
        //Focusを失った時に選択解除する
        branchTree.focusedProperty().addListener { _, _, newValue ->
            if(!newValue) branchTree.selectionModel.clearSelection() }
        //初期状態では不可視
        branchTree.isVisible = false
    }

    //表示完了時にListenerを設定する
    override fun displayCompleted() {
        val branchList = GviewRepositoryModel.currentRepository.branches
        //リポジトリ内のLocal/Remoteブランチ情報更新時に再描画する
        branchList.addListener {
            updateLocalBranches (it.localBranches)
            updateRemoteBranches(it.remoteBranches)
            branchTree.selectionModel.clearSelection()
        }
    }

    val selectedBranch: GviewBranchModel? get() {
        return branchTree.selectionModel.selectedItem?.value
    }

    //BranchTreeに描画するTreeCellクラス
    private class BranchTreeCell
        : TreeCell<GviewBranchModel>() {

        override fun updateItem(model: GviewBranchModel?, empty: Boolean) {
            super.updateItem(model, empty)
            if(!empty) {
                graphic = (treeItem as? BranchTreeItem)?.cellImage
                contextMenu = (treeItem as? BranchTreeItem)?.contextMenu
            } else {
                graphic = null
                contextMenu = null
            }
            text = null
            style = CSS.cellStyle
        }
    }

    //ローカルブランチツリーを更新する
    private fun updateLocalBranches(
            branches: List<GviewLocalBranchModel>) {

        //ローカルブランチ一覧からツリー項目を生成
        localTreeRoot.children.clear()
        branches.forEach {
            val item = LocalBranchItem(it)
            localTreeRoot.children.add(item)
        }

        //表示対象のローカルブランチ一覧を初期化
        branchTree.isVisible = true
    }

    //リモートブランチツリーを更新する
    private fun updateRemoteBranches(
            branches: List<GviewRemoteBranchModel>) {

        remoteTreeRoot.children.clear()
        branches.forEach { remoteTreeRoot.children.add(RemoteBranchItem(it)) }
        branchTree.isVisible = true
    }

    //ブランチツリーの基本クラス
    abstract class BranchTreeItem(
            model: GviewBranchModel?)
        : TreeItem<GviewBranchModel>(model) {

        abstract val cellImage: Node                    //表示イメージ
        abstract val contextMenu: ContextMenu?          //コンテキストメニュー
        abstract override fun isLeaf(): Boolean         //末端か
    }

    //Remote/Localそれぞれのルートになるコンポーネント
    class RootItem(name: String)
        : BranchTreeItem(null) {

        override val cellImage: Node = Label(name)
        override val contextMenu: ContextMenu? = null
        override fun isLeaf(): Boolean = false
        init { isExpanded = true }
    }

    private object CSS {
        val treeStyle = """
            -fx-font-family: "Meiryo UI", sans-serif;
            -fx-background-color: -background-color;
            -fx-padding: 0;
        """.trimIndent()
        val cellStyle = """
            -fx-padding: 2 0 2 0;
        """.trimIndent()
    }
}
