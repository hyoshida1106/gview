package gview.gui.branchlist

import gview.getCurrentRepository
import gview.gui.framework.BaseCtrl
import gview.model.branch.GviewBranchModel
import gview.model.branch.GviewLocalBranchModel
import gview.model.branch.GviewRemoteBranchModel
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*

/*
    ブランチ一覧 Control
 */
class BranchListCtrl : BaseCtrl() {

    //ブランチ一覧Tree
    @FXML private lateinit var branchTree: TreeView<GviewBranchModel>

    //ローカルブランチTree
    private var localTreeRoot = RootItem("Local")

    //リモートブランチTree
    private var remoteTreeRoot = RootItem("Remote")

    //表示対象のローカルブランチ一覧
    var selectedBranches = SimpleObjectProperty<List<GviewLocalBranchModel>>()

    //View初期化
    fun initialize() {
        //Root Treeを作成
        val root = RootItem("Branch Root" )
        root.children.addAll(localTreeRoot, remoteTreeRoot)
        //ブランチTreeの初期設定
        branchTree.root = root
        branchTree.isShowRoot = false
        branchTree.setCellFactory { _ -> BranchTreeCell() }
        branchTree.selectionModel.clearSelection()
        //Focusを失った時に選択解除する
        branchTree.focusedProperty().addListener { _, _, newv ->
            if(!newv) branchTree.selectionModel.clearSelection() }
        //初期状態では不可視
        branchTree.isVisible = false
    }

    //表示完了時にListenerを設定する
    override fun displayCompleted() {
        val branchList = getCurrentRepository().branchList
        //リポジトリ内のLocal/Remoteブランチ情報更新時に再描画する
        branchList.localBranchesProperty .addListener { _, _, newVal -> updateLocalBranches (newVal) }
        branchList.remoteBranchesProperty.addListener { _, _, newVal -> updateRemoteBranches(newVal) }
    }

    //BranchTreeに描画するTreeCellクラス
    private class BranchTreeCell: TreeCell<GviewBranchModel>() {
        override fun updateItem(model: GviewBranchModel?, empty: Boolean) {
            super.updateItem(model, empty)
            graphic = if(!empty) { (treeItem as? BranchTreeItem)?.cellImage } else { null }
            text = null
            contextMenu = (treeItem as? BranchTreeItem)?.contextMenu
        }
    }

    //ローカルブランチツリーを更新する
    private fun updateLocalBranches(branches: List<GviewLocalBranchModel>) {
        //ローカルブランチ一覧からツリー項目を生成
        localTreeRoot.children.clear()
        branches.forEach {
            val item = LocalBranchItem(it)
            localTreeRoot.children.add(item)
            //チェックボックス更新時の処理を登録
            item.selectedProperty.addListener { _ -> setSelectedLocalBranchList() }
        }
        //表示対象のローカルブランチ一覧を初期化
        setSelectedLocalBranchList()
        branchTree.isVisible = true
    }

    //リモートブランチツリーを更新する
    private fun updateRemoteBranches(branches: List<GviewRemoteBranchModel>) {
        remoteTreeRoot.children.clear()
        branches.forEach { remoteTreeRoot.children.add(RemoteBranchItem(it)) }
        branchTree.isVisible = true
    }

    //表示対象のローカルブランチ一覧
    private fun setSelectedLocalBranchList() {
        val branches = mutableListOf<GviewLocalBranchModel>()
        localTreeRoot.children.forEach {
            val localItem = it as? LocalBranchItem
            if(localItem != null && localItem.isSelected) {
                branches.add(localItem.model)
            }
        }
        selectedBranches.value = branches
    }

    //ブランチツリーの基本クラス
    abstract class BranchTreeItem(model: GviewBranchModel?): TreeItem<GviewBranchModel>(model) {
        abstract val cellImage: Node                    //表示イメージ
        abstract val contextMenu: ContextMenu?          //コンテキストメニュー
        abstract override fun isLeaf(): Boolean         //末端か
    }

    //Remote/Localそれぞれのルートになるコンポーネント
    class RootItem(name: String): BranchTreeItem(null) {
        override val cellImage: Node = Label(name)
        override val contextMenu: ContextMenu? = null
        override fun isLeaf(): Boolean = false
        init {
            isExpanded = true
        }
    }
}