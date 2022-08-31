package gview.view.dialog

import gview.view.framework.GvCustomDialogCtrl
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.fxml.FXML
import javafx.scene.control.TextField

class RenameBranchDialogCtrl : GvCustomDialogCtrl() {
    @FXML private lateinit var branchName: TextField

    /**
     *  OKボタンの無効を指示するプロパティ
     */
    val btnOkDisable = SimpleBooleanProperty(true)

    /**
     * 設定されたブランチ名称を参照する
     */
    var newBranchName: String
        get() = branchName.text.trim()
        set(value) { branchName.text = value }

    /**
     * 初期化
     */
    override fun initialize() {
        branchName.lengthProperty().addListener { _ -> btnOkDisable.value = newBranchName.isEmpty() }
        Platform.runLater { branchName.requestFocus() }
    }
}