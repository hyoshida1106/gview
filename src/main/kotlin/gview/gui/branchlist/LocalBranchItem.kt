package gview.gui.branchlist

import gview.model.branch.GviewLocalBranchModel
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.layout.HBox

/*
    ローカルブランチTree Item
 */
class LocalBranchItem(val model: GviewLocalBranchModel): BranchListCtrl.BranchTreeItem(model) {
    private  val branchName = Label(model.name)
    private  val showInTree = CheckBox()
    override val cellImage: Node = HBox(branchName, showInTree)
    override fun isLeaf(): Boolean = true
    override val contextMenu: ContextMenu? = null

    //初期化
    init {
        showInTree.isAllowIndeterminate = false
        showInTree.isSelected = model.selected
        showInTree.style = CSS.checkBoxStyle
        //チェックボックス選択状態プロパティを接続
        model.selectedProperty.bind(showInTree.selectedProperty())
    }

    private object CSS {
        val checkBoxStyle = """
             -fx-padding: 0 0 0 10;
        """.trimIndent()
    }
}
