package gview.gui.dialog

import gview.gui.framework.GviewCustomDialogCtrl
import javafx.fxml.FXML
import javafx.scene.control.TextArea

class MergeDialogCtrl: GviewCustomDialogCtrl() {

    @FXML private lateinit var messageText: TextArea

    //初期化
    override fun initialize() {
    }

    //メッセージ
    val message: String get() = messageText.text

}
