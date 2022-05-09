package gview.view.branchlist

import gview.view.menu.LocalBranchContextMenu
import gview.model.branch.GvLocalBranch
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.HBox

/*
    ローカルブランチTree Item
 */
class LocalBranchItem(val model: GvLocalBranch) : BranchListCtrl.BranchTreeItem(model) {

    private  val branchName = Label(model.name)
    private  val showInTree = CheckBox()
    override val cellImage: Node = HBox(branchName, showInTree)
    override fun isLeaf(): Boolean = true
    override val contextMenu: ContextMenu? get()  { return LocalBranchContextMenu(model) }

    //初期化
    init {
        if(model.isCurrentBranch) {
            branchName.style = Style.currentBranchLabelStyle
            showInTree.isSelected = true
        }
        showInTree.style = Style.checkBoxStyle
        showInTree.isSelected = model.selectedFlagProperty.value
        showInTree.selectedProperty().addListener {
                _, _, newVal -> model.selectedFlagProperty.set(newVal) }
    }

    private object Style {
        val currentBranchLabelStyle =
            "-fx-font-weight: bold;"
        val checkBoxStyle =
            "-fx-padding: 0 0 0 2;"
    }
}
