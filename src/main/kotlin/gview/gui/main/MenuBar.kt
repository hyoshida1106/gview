package gview.gui.main

import gview.gui.framework.GviewBasePane
import gview.gui.framework.GviewBasePaneCtrl
import gview.gui.framework.GviewCommonDialog
import javafx.fxml.FXML
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar as JavaFxMenuBar
import javafx.scene.control.MenuItem
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import kotlin.system.exitProcess

object MenuBar: GviewBasePane<MenuBarCtrl>(
        "/view/MenuBar.fxml",
        "MenuBar")

class MenuBarCtrl : GviewBasePaneCtrl() {

    @FXML private lateinit var menuBar: JavaFxMenuBar

    //初期化
    fun initialize() {
        menuBar.style = CSS.menuBarStyle
        menuBar.menus.addAll(
            FileMenu.menu,
            BranchMenu.menu,
            WorkTreeMenu.menu,
            CommitMenu.menu,
            HelpMenu.menu
        )
    }

    private object CSS {
        val menuBarStyle = """
            -fx-background-color: -background-color;
            -fx-effect: innershadow(three-pass-box, gray, 3, 0.5, 1, 1);
        """.trimIndent()
    }
}
