package gview.gui.branchlist

import gview.gui.menu.LocalBranchContextMenu
import gview.model.branch.GviewLocalBranchModel
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.HBox

/*
    ローカルブランチTree Item
 */
class LocalBranchItem(val model: GviewLocalBranchModel, toggleGroup: ToggleGroup): BranchListCtrl.BranchTreeItem(model) {
    private  val branchName = Label(model.name)
    private  val showInTree = RadioButton()
    override val cellImage: Node = HBox(branchName, showInTree)
    override fun isLeaf(): Boolean = true
    override val contextMenu: ContextMenu? get()  { return LocalBranchContextMenu(model) }

    //初期化
    init {
        if(model.isCurrentRepository) {
            branchName.style = CSS.currentBranchLabelStyle
            showInTree.isSelected = true
        }
        showInTree.toggleGroup = toggleGroup
        showInTree.style = CSS.radioButtonStyle
        showInTree.selectedProperty().addListener { _, _, newVal -> model.selected = newVal }
    }

    private object CSS {
        val currentBranchLabelStyle = """
            -fx-font-weight: bold;
        """.trimIndent()
        val radioButtonStyle = """
            -fx-padding: 0 0 0 2;
        """.trimIndent()
    }
}
