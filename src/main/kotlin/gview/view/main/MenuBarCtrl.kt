package gview.view.main

import gview.view.framework.GvBaseWindowCtrl
import gview.view.menu.*
import javafx.fxml.FXML
import javafx.scene.control.MenuBar as JavaFxMenuBar

class MenuBarCtrl
    : GvBaseWindowCtrl() {

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
