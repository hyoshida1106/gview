package gview.view.dialog

import gview.view.framework.GvCustomDialogCtrl
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField

class BranchNameDialogCtrl : GvCustomDialogCtrl() {
    @FXML private lateinit var branchName: TextField
    @FXML private lateinit var checkout: CheckBox

    /**
     *  OKボタンの無効を指示するプロパティ
     */
    val btnOkDisable = SimpleBooleanProperty(true)

    /**
     * 設定されたブランチ名称を参照する
     */
    val newBranchName get() = branchName.text.trim()

    /**
     *  チェックアウトフラグの設定結果を参照する
     */
    val checkoutFlag: Boolean get() = checkout.isSelected

    /**
     * 初期化
     */
    override fun initialize() {
        checkout.isSelected = true
        branchName.text = ""
        branchName.lengthProperty().addListener { _ -> btnOkDisable.value = branchName.text.isBlank() }
        Platform.runLater { branchName.requestFocus() }
    }
}