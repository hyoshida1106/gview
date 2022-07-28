package gview.view.dialog

import gview.model.branch.GvBranch
import gview.resourceBundle
import gview.view.framework.GvDialogInterface
import gview.view.main.MainWindow
import javafx.scene.control.ChoiceDialog

class BranchSelectDialog(list: List<GvBranch>) : ChoiceDialog<String>(), GvDialogInterface<GvBranch?> {
	private val branchMap: Map<String, GvBranch>

	init {
		initOwner(MainWindow.root.scene.window)
		title = resourceBundle().getString("BranchSelect.Title")
		graphic = null
		headerText = null
		contentText = resourceBundle().getString("BranchSelect.Checkout")
		branchMap = list.associateBy { it.name }
		items.addAll(branchMap.keys)
	}

	override fun showDialog(): GvBranch? {
		val result = showAndWait()
		return if (result.isPresent) branchMap[result.get()] else null
	}
}