package gview.gui.branchlist

import gview.model.branch.GviewLocalBranchModel
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
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

    //チェックボックス選択状態を取得するProperty
    val selectedProperty: BooleanProperty
    val isSelected: Boolean get() { return selectedProperty.value }

    //初期化
    init {
        showInTree.isAllowIndeterminate = false
        showInTree.isSelected = true
        selectedProperty = SimpleBooleanProperty(true)
        //チェックボックス選択状態プロパティを接続
        selectedProperty.bind(showInTree.selectedProperty())
    }
}
