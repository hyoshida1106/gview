package gview.gui.dialog

import gview.gui.framework.GviewDialog
import gview.gui.framework.GviewDialogController
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.fxml.FXML
import javafx.scene.control.ButtonType
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane


class UserNameDialog(userName: String, mailAddr: String)
    : GviewDialog<UserNameDialogCtrl>(
        "ユーザ名とパスワードを入力してください",
        "/dialog/UserNameDialog.fxml",
        ButtonType.OK, ButtonType.CANCEL) {

    private val userNameProperty = SimpleStringProperty(userName)
    val userName:String get() = userNameProperty.value

    private val mailAddrProperty = SimpleStringProperty(mailAddr)
    val mailAddr:String get() = mailAddrProperty.value

    init {
        controller.userNameProperty.bindBidirectional(userNameProperty)
        controller.mailAddrProperty.bindBidirectional(mailAddrProperty)
    }
}

class UserNameDialogCtrl : GviewDialogController() {

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
