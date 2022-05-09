package gview.view.branchlist

import gview.view.framework.GvBaseWindowCtrl
import gview.model.GvRepository
import gview.model.branch.GvBranch
import gview.model.branch.GvBranchList
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*

class BranchListCtrl: GvBaseWindowCtrl() {

    //ブランチ一覧Tree
    @FXML private lateinit var branchTree: TreeView<GvBranch>

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
        branchTree.style = Style.treeStyle
        GvRepository.currentRepositoryProperty.addListener { _, _, repository
            -> Platform.runLater { updateRepository(repository) }
        }

        //Focusを失った時に選択解除する
        branchTree.focusedProperty().addListener { _, _, newValue ->
            if(!newValue) branchTree.selectionModel.clearSelection() }
        //初期状態では不可視
        branchTree.isVisible = false
    }

    private fun updateRepository(repository: GvRepository) {
        val branchList = repository.branches
        branchList.localBranchList.addListener  { _ -> updateLocalBranches (branchList) }
        branchList.remoteBranchList.addListener { _ -> updateRemoteBranches(branchList) }
        branchList.currentBranch.addListener    { _ -> updateLocalBranches (branchList) }
        updateLocalBranches (branchList)
        updateRemoteBranches(branchList)
        branchTree.selectionModel.clearSelection()
    }

    val selectedBranch: GvBranch? get() {
        return branchTree.selectionModel.selectedItem?.value
    }

    //BranchTreeに描画するTreeCellクラス
    private class BranchTreeCell: TreeCell<GvBranch>() {

        override fun updateItem(model: GvBranch?, empty: Boolean) {
            super.updateItem(model, empty)
            if(!empty) {
                graphic = (treeItem as? BranchTreeItem)?.cellImage
                contextMenu = (treeItem as? BranchTreeItem)?.contextMenu
            } else {
                graphic = null
                contextMenu = null
            }
            text = null
            style = Style.cellStyle
        }
    }

    //ローカルブランチツリーを更新する
    private fun updateLocalBranches(branchList: GvBranchList) {
        localTreeRoot.children.clear()
        branchList.localBranchList.value.forEach { localTreeRoot.children.add(LocalBranchItem(it)) }
        branchTree.isVisible = true
    }

    //リモートブランチツリーを更新する
    private fun updateRemoteBranches(branchList: GvBranchList) {
        remoteTreeRoot.children.clear()
        branchList.remoteBranchList.value.forEach { remoteTreeRoot.children.add(RemoteBranchItem(it)) }
        branchTree.isVisible = true
    }

    //ブランチツリーの基本クラス
    abstract class BranchTreeItem(model: GvBranch?) : TreeItem<GvBranch>(model) {
        abstract val cellImage: Node                    //表示イメージ
        abstract val contextMenu: ContextMenu?          //コンテキストメニュー
        abstract override fun isLeaf(): Boolean         //末端か
    }

    //Remote/Localそれぞれのルートになるコンポーネント
    class RootItem(name: String) : BranchTreeItem(null) {
        override val cellImage: Node = Label(name)
        override val contextMenu: ContextMenu? = null
        override fun isLeaf(): Boolean = false
        init { isExpanded = true }
    }

    private object Style {
        val treeStyle =
            "-fx-padding: 0;"
        val cellStyle =
            "-fx-padding: 2 0;"
    }
}
