package gview.view.main

import gview.view.framework.GvBaseWindowCtrl
import gview.view.menu.*
import javafx.fxml.FXML
import javafx.scene.control.MenuBar as JavaFxMenuBar

/**
 * メニューバーコントロールクラス
 */
class MenuBarCtrl : GvBaseWindowCtrl() {
    @FXML private lateinit var menuBar: JavaFxMenuBar

    /**
     * 初期化
     */
    fun initialize() {
        menuBar.menus.addAll(
            FileMenu(),
            RepositoryMenu(),
            BranchMenu(),
            WorkTreeMenu(),
            CommitMenu(),
            HelpMenu()
        )
    }
}
