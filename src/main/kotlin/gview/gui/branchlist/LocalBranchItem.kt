package gview.gui.branchlist

import gview.gui.menu.LocalBranchContextMenu
import gview.model.branch.GviewLocalBranchModel
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.HBox

/*
    ローカルブランチTree Item
 */
class LocalBranchItem(val model: GviewLocalBranchModel)
    : BranchListCtrl.BranchTreeItem(model) {

    private  val branchName = Label(model.name)
    private  val showInTree = CheckBox()
    override val cellImage: Node = HBox(branchName, showInTree)
    override fun isLeaf(): Boolean = true
    override val contextMenu: ContextMenu? get()  { return LocalBranchContextMenu(model) }

    //初期化
    init {
        if(model.isCurrentBranch) {
            branchName.style = CSS.currentBranchLabelStyle
            showInTree.isSelected = true
        }
        showInTree.style = CSS.checkBoxStyle
        showInTree.isSelected = model.selected
        showInTree.selectedProperty().addListener { _, _, newVal -> model.selected = newVal }
    }

    private object CSS {
        val currentBranchLabelStyle = """
            -fx-font-weight: bold;
        """.trimIndent()
        val checkBoxStyle = """
            -fx-padding: 0 0 0 2;
        """.trimIndent()
    }
}
