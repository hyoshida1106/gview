package gview.gui.menu

import gview.gui.framework.GviewBaseMenu
import javafx.fxml.FXML
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem

object BranchMenu: GviewBaseMenu<BranchMenuCtrl>("/menu/BranchMenu.fxml")

class BranchMenuCtrl {

    @FXML private lateinit var branchMenu: Menu
    @FXML private lateinit var branchCheckoutMenu: MenuItem
    @FXML private lateinit var branchPushMenu: MenuItem
    @FXML private lateinit var branchPullMenu: MenuItem
    @FXML private lateinit var branchRenameMenu: MenuItem
    @FXML private lateinit var branchRemoveMenu: MenuItem

    //メニュー表示
    @FXML private fun onShowingMenu() {
        branchCheckoutMenu.isDisable = true
        branchPushMenu.isDisable = true
        branchPullMenu.isDisable = true
        branchRenameMenu.isDisable = true
        branchRemoveMenu.isDisable = true
    }

}

