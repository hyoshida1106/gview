package gview.view.menu

import javafx.event.EventHandler
import javafx.scene.control.Menu


class HelpMenu: Menu("ヘルプ(_H)") {

    private val helpMenuItem = GviewMenuItem(
            text = "ヘルプ...",
            iconLiteral = "mdi-help-circle-outline"
    ) { onHelp() }

    private val aboutMenuItem = GviewMenuItem(
            text = "プログラムについて..."
    ) { onAbout() }

    init {
        items.setAll(
            helpMenuItem,
            aboutMenuItem
        )
        onShowing = EventHandler { onMyShowing() }
    }

    private fun onMyShowing() {
        aboutMenuItem.isDisable = true
    }

    private fun onHelp() {
        println("Help")
    }

    private fun onAbout() {
        println("About")
    }
}