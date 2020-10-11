package gview.gui.menu

import gview.gui.dialog.BranchNameDialog
import gview.gui.dialog.ErrorDialog
import gview.gui.framework.GviewMenuItem
import gview.model.GviewRepositoryModel
import gview.model.commit.GviewCommitDataModel
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import java.lang.Exception

class CommitRowContextMenu(private val model: GviewCommitDataModel)
    : ContextMenu() {

    private val createBranchMenu = GviewMenuItem(
        text = "このコミットからブランチを作成する...",
        iconLiteral = "mdi-source-branch"
    ) { onCreateBranch() }

    init {
        items.setAll(
                createBranchMenu
        )
        onShowing = EventHandler { onMyShowing() }
    }

    private fun onMyShowing() {
    }

    private fun onCreateBranch() {
        val branchName = BranchNameDialog().showDialog()
        if(branchName != null) {
            try {
                GviewRepositoryModel.currentRepository.branches
                        .createNewBranchFromCommit(branchName, model)
            } catch(e: Exception) {
                ErrorDialog(e).showDialog()
            }
        }
    }
}