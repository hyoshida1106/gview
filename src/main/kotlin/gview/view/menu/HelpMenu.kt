package gview.view.menu

import gview.resourceBundle
import javafx.event.EventHandler
import javafx.scene.control.Menu

class HelpMenu: Menu(resourceBundle().getString("HelpMenu.Title")) {

    private val helpMenuItem = GvMenuItem(
            text = resourceBundle().getString("HelpMenu.Help"),
            iconLiteral = "mdi2h-help-circle-outline"   // NON-NLS
    ) {  }

    private val aboutMenuItem = GvMenuItem(
            text = resourceBundle().getString("HelpMenu.Program")
    ) {  }

    init {
        items.setAll(
            helpMenuItem,
            aboutMenuItem
        )
        onShowing = EventHandler { onMyShowing() }
    }

    private fun onMyShowing() {
        helpMenuItem.isDisable = true
        aboutMenuItem.isDisable = true
    }
}