package gview.view.branchlist

import gview.view.framework.GvBaseWindowCtrl
import gview.model.GvRepository
import gview.model.branch.GvBranch
import gview.model.branch.GvBranchList
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
        localTreeRoot.children.setAll(localBranchList.map { LocalBranchItem(it) } )
    }

    private fun updateRemoteBranches(remoteBranchList: List<GvRemoteBranch>) {
        remoteTreeRoot.children.setAll(remoteBranchList.map { RemoteBranchItem(it) })
    }

    val selectedBranch: GvBranch? get() {
        return branchTree.selectionModel.selectedItem?.value
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
        abstract override fun isLeaf(): Boolean
    }

    class RootItem(name: String) : BranchTreeItem(null) {
        override val cellImage: Node = Label(name)
        override val contextMenu: ContextMenu? = null
        override fun isLeaf(): Boolean = false
        init { isExpanded = true }
    }

    class RemoteBranchItem(val model: GvRemoteBranch) : BranchTreeItem(model) {
        override val cellImage: Node = Label(model.name)
        override val contextMenu: ContextMenu? = RemoteBranchContextMenu(model)
        override fun isLeaf(): Boolean = true
    }

    class LocalBranchItem(val model: GvLocalBranch) : BranchTreeItem(model) {
        private val branchName = Label(model.name)
        private val showInTree = CheckBox()
        override val cellImage: Node = HBox(branchName, showInTree)
        override val contextMenu: ContextMenu? = LocalBranchContextMenu(model)
        override fun isLeaf(): Boolean = true

        init {
            if (model.isCurrentBranch) {
                branchName.styleClass.add("CurrentBranch")
                showInTree.isSelected = true
            }
            showInTree.isSelected = model.selectedFlagProperty.value
            if (model.isCurrentBranch) {
                showInTree.isDisable = true
            } else {
                showInTree.isDisable = false
                showInTree.selectedProperty().addListener { _, _, newVal -> model.selectedFlagProperty.set(newVal) }
            }
        }
    }
}
