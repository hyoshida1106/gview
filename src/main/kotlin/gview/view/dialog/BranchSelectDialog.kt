package gview.view.dialog

import gview.view.framework.GvDialogInterface
import gview.view.main.MainWindow
import gview.model.branch.GviewLocalBranchModel
import javafx.scene.control.ChoiceDialog

class BranchSelectDialog(private val list: List<GviewLocalBranchModel>)
	: ChoiceDialog<String>(),
		GvDialogInterface<GviewLocalBranchModel?> {

	private val branchMap: Map<String, GviewLocalBranchModel>

	init {
		initOwner(MainWindow.root.scene.window)
		title = "ブランチ選択"
		graphic = null
		headerText = null
		contentText = "チェックアウトするブランチ"
		branchMap = list
				.filter { !it.isCurrentBranch }
				.map { it.name to it }.toMap()
		items.addAll( branchMap.keys )
	}

	override fun showDialog(): GviewLocalBranchModel? {
		val result = showAndWait()
		return if (result.isPresent) branchMap[result.get()] else null
	}
}