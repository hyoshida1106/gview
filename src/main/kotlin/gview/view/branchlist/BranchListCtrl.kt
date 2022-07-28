package gview.view.branchlist

import gview.view.framework.GvBaseWindowCtrl
import gview.model.GvRepository
import gview.model.branch.GvBranch
import gview.model.branch.GvLocalBranch
import gview.model.branch.GvRemoteBranch
import gview.view.main.MainWindow
import gview.view.menu.LocalBranchContextMenu
import gview.view.menu.RemoteBranchContextMenu
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.MouseButton
import javafx.scene.layout.HBox

/**
 * ブランチ一覧
 */
class BranchListCtrl: GvBaseWindowCtrl() {
    @FXML private lateinit var branchTree: TreeView<GvBranch>

    private var localTreeRoot  = RootItem("Local")
    private var remoteTreeRoot = RootItem("Remote")

    fun initialize() {
        val root = RootItem("Branch Root" )
        root.children.setAll(localTreeRoot, remoteTreeRoot)

        branchTree.root = root
        branchTree.isShowRoot = false
        branchTree.setCellFactory { BranchTreeCell() }
        branchTree.selectionModel.clearSelection()
        GvRepository.currentRepositoryProperty.addListener { _, _, repository
            -> Platform.runLater { updateRepository(repository) }
        }

        branchTree.focusedProperty().addListener { _, _, newValue ->
            if(!newValue) branchTree.selectionModel.clearSelection() }

        branchTree.setOnMouseClicked {
            if(it.button == MouseButton.PRIMARY && it.clickCount == 2) {
                val model = selectedBranch
                if(model is GvLocalBranch) {
                    MainWindow.controller.runTask { model.checkout() }
                }
            }
        }

        branchTree.isVisible = false
    }

    val selectedBranch: GvBranch? get() =branchTree.selectionModel.selectedItem?.value

    private fun updateRepository(repository: GvRepository) {
        val branchList = repository.branches
        updateLocalBranches(branchList.localBranchList.value)
        updateRemoteBranches(branchList.remoteBranchList.value)
        branchList.localBranchList.addListener { _ -> updateLocalBranches(branchList.localBranchList.value) }
        branchList.remoteBranchList.addListener { _ -> updateRemoteBranches(branchList.remoteBranchList.value) }
        branchList.currentBranch.addListener { _ -> updateLocalBranches(branchList.localBranchList.value) }
        branchTree.selectionModel.clearSelection()
        branchTree.isVisible = true
    }

    private fun updateLocalBranches(localBranchList: List<GvLocalBranch>) {
        localTreeRoot.children.clear()
        localBranchList.forEach { model ->
            val leaf = LocalBranchItem(model)
            findTreePath(localTreeRoot, leaf).children.add(leaf)
        }
    }

    private fun updateRemoteBranches(remoteBranchList: List<GvRemoteBranch>) {
        remoteTreeRoot.children.clear()
        remoteBranchList.forEach { model ->
            val leaf = RemoteBranchItem(model)
            findTreePath(remoteTreeRoot, leaf).children.add(leaf)
        }
    }

    private fun findTreePath(root: RootItem, leaf: LeafItem): RootItem {
        var node: RootItem = root
        leaf.path.forEach { name ->
            var n = node.children.find { p -> (p as? BranchTreeItem)?.name == name } as? RootItem
            if(n == null) {
                n = RootItem(name)
                node.children.add(n)
            }
            node = n
        }
        return node
    }

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
        }
    }

    abstract class BranchTreeItem(model: GvBranch?) : TreeItem<GvBranch>(model) {
        abstract val cellImage: Node
        abstract val contextMenu: ContextMenu?
        abstract val name: String
        abstract val path: List<String>
        abstract override fun isLeaf(): Boolean
    }

    class RootItem(name: String) : BranchTreeItem(null) {
        override val cellImage: Node = Label(name)
        override val contextMenu: ContextMenu? = null
        override val name: String = name
        override val path:List<String> = emptyList()
        override fun isLeaf(): Boolean = false
        init { isExpanded = true }
    }

    abstract class LeafItem(model: GvBranch): BranchTreeItem(model) {
        private val pathList: List<String> = model.path.split("/")
        override val name: String = pathList.last()
        override val path: List<String> = pathList.drop(2).dropLast(1)
    }

    class RemoteBranchItem(val model: GvRemoteBranch) : LeafItem(model) {
        override val cellImage: Node = Label(name)
        override val contextMenu: ContextMenu? = RemoteBranchContextMenu(model)
        override fun isLeaf(): Boolean = true
    }

    class LocalBranchItem(val model: GvLocalBranch) : LeafItem(model) {
        private val branchName = Label(name)
        private val showInTree = CheckBox()
        override val cellImage: Node = HBox(branchName, showInTree)
        override val contextMenu: ContextMenu? = LocalBranchContextMenu(model)
        override fun isLeaf(): Boolean = true

        init {
            if (model.isCurrentBranch) {
                branchName.styleClass.add("CurrentBranch")
                showInTree.isSelected = true
                showInTree.isDisable = true
            } else {
                showInTree.isSelected = model.selectedFlagProperty.value
                showInTree.isDisable = false
                showInTree.selectedProperty().addListener { _, _, newVal -> model.selectedFlagProperty.set(newVal) }
            }
        }
    }
}
