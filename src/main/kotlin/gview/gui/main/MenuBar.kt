package gview.gui.main

import gview.gui.framework.GviewBasePane
import gview.gui.framework.GviewBasePaneCtrl
import gview.gui.menu.*
import javafx.fxml.FXML
import javafx.scene.control.MenuBar as JavaFxMenuBar

object MenuBar: GviewBasePane<MenuBarCtrl>(
        "/view/MenuBar.fxml",
        "MenuBar")

class MenuBarCtrl : GviewBasePaneCtrl() {

    @FXML private lateinit var menuBar: JavaFxMenuBar

    //初期化
    fun initialize() {
        menuBar.style = CSS.menuBarStyle
        menuBar.menus.addAll(
            FileMenu(),
            BranchMenu(),
            WorkTreeMenu(),
            CommitMenu(),
            HelpMenu()
        )
    }

    private object CSS {
        val menuBarStyle = """
            -fx-background-color: -background-color;
            -fx-effect: innershadow(three-pass-box, gray, 3, 0.5, 1, 1);
        """.trimIndent()
    }
}
