package gview.view.dialog

import gview.model.branch.GvLocalBranch
import gview.resourceBundle
import gview.view.framework.GvCustomDialog
import javafx.scene.control.ButtonType

class SelectBranchDialog(branches: List<GvLocalBranch>): GvCustomDialog<SelectBranchDialogCtrl>(
	resourceBundle().getString("SelectBranchDialog.Title"),
	"/dialog/SelectBranchDialog.fxml",		  					// NON-NLS
	SelectBranchDialogCtrl(branches),
	ButtonType.OK, ButtonType.CANCEL) {

	init {
		addButtonHandler(ButtonType.OK, controller.btnOkDisable)
	}
}