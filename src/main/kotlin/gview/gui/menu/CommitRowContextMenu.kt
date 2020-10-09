package gview.gui.menu

import gview.gui.framework.GviewMenuItem
import gview.model.commit.GviewCommitDataModel
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu

class CommitRowContextMenu(model: GviewCommitDataModel)
    : ContextMenu() {

    private val createBranchMenu = GviewMenuItem(
            "このコミットからブランチを作成する"
    ) {  }

    init {
        items.setAll(
                createBranchMenu
        )
        onShowing = EventHandler { onMyShowing() }
    }

    private fun onMyShowing() {
    }
}