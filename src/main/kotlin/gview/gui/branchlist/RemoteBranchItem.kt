package gview.gui.branchlist

import gview.gui.menu.RemoteBranchContextMenu
import gview.model.branch.GviewRemoteBranchModel
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label

class RemoteBranchItem(
        val model: GviewRemoteBranchModel)
    : BranchListCtrl.BranchTreeItem(model) {

    override val cellImage = Label(model.name)
    override fun isLeaf(): Boolean = true
    override val contextMenu: ContextMenu? get()  { return RemoteBranchContextMenu(model) }
}
