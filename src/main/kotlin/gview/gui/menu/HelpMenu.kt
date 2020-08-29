package gview.gui.menu

import gview.gui.framework.GviewBaseMenu
import javafx.fxml.FXML
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem

object HelpMenu: GviewBaseMenu<HelpMenuCtrl>("/menu/HelpMenu.fxml")

class HelpMenuCtrl {

    @FXML private lateinit var helpMenu: Menu
    @FXML private lateinit var helpHelpMenu: MenuItem
    @FXML private lateinit var helpAboutMenu: MenuItem

    @FXML private fun onShowingMenu() {
        helpHelpMenu.isDisable = true
        helpAboutMenu.isDisable = true
    }
}
