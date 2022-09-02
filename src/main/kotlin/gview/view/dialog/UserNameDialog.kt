package gview.view.dialog

import gview.resourceBundle
import gview.view.framework.GvCustomDialog
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.ButtonType

class UserNameDialog(userName: String, mailAddress: String) : GvCustomDialog<UserNameDialogCtrl>(
    resourceBundle().getString("UserNameDialog.Title"),
    "/dialog/UserNameDialog.fxml",          // NON-NLS
    UserNameDialogCtrl(),
    ButtonType.OK, ButtonType.CANCEL
) {

    private val userNameProperty = SimpleStringProperty(userName)
    val userName: String get() = userNameProperty.value

    private val mailAddrProperty = SimpleStringProperty(mailAddress)
    val mailAddr: String get() = mailAddrProperty.value

    init {
        controller.userNameProperty.bindBidirectional(userNameProperty)
        controller.mailAddrProperty.bindBidirectional(mailAddrProperty)
    }
}
