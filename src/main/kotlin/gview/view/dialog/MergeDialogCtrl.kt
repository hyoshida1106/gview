package gview.view.dialog

import gview.view.framework.GvCustomDialogCtrl
import javafx.fxml.FXML
import javafx.scene.control.TextArea

class MergeDialogCtrl : GvCustomDialogCtrl() {

    @FXML private lateinit var messageText: TextArea

    //初期化
    override fun initialize() {
    }

    //メッセージ
    val message: String get() = messageText.text
}
