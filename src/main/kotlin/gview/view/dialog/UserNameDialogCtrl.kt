package gview.view.dialog

import gview.view.framework.GvCustomDialogCtrl
import javafx.beans.property.StringProperty
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane

class UserNameDialogCtrl
    : GvCustomDialogCtrl() {

    @FXML private lateinit var pane: GridPane
    @FXML private lateinit var userName: TextField
    @FXML private lateinit var mailAddr: TextField

    val userNameProperty: StringProperty get() = userName.textProperty()
    val mailAddrProperty: StringProperty get() = mailAddr.textProperty()

    //初期化
    override fun initialize() {
        pane.style = CSS.paneStyle
    }

    object CSS {
        val paneStyle = """
            -fx-padding: 0 0 0 30;
        """.trimIndent()
    }
}
