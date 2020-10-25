package gview.gui.dialog

import gview.gui.framework.GviewCustomDialogCtrl
import javafx.beans.property.SimpleBooleanProperty
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField

class BranchNameDialogCtrl
    : GviewCustomDialogCtrl() {

    @FXML private lateinit var branchName: TextField
    @FXML private lateinit var checkout: CheckBox

    //OKボタンの無効を指示するプロパティ
    val btnOkDisable = SimpleBooleanProperty(true)

    //ブランチ名称
    val newBranchName get() = branchName.text.trim()

    //チェックアウトフラグ
    val checkoutFlag: Boolean get() = checkout.isSelected

    //初期化
    override fun initialize() {
        checkout.isSelected = true
        branchName.text = ""
        branchName.lengthProperty().addListener { _ -> btnOkDisable.value = branchName.text.isBlank() }
    }
}