package gview.gui.dialog

import gview.gui.framework.GviewCustomDialog
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.ButtonType

class UserNameDialog(userName: String, mailAddr: String)
    : GviewCustomDialog<UserNameDialogCtrl>(
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
