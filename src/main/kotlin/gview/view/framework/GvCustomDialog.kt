package gview.view.framework

import javafx.beans.property.BooleanProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog

/**
 * カスタムダイアログ
 *
 * @param title     ダイアログのタイトル文字列
 * @param form      FXMLファイルのパス
 * @param buttons   表示するボタン
 */
open class GvCustomDialog<Controller>(title: String, form: String, vararg buttons: ButtonType)
    : Dialog<ButtonType>(), GvDialogInterface<ButtonType?> where Controller: GvCustomDialogCtrl {

    /**
     * コントローラへの参照を保持する
     */
    val controller: Controller

    /**
     * 初期化
     */
    init {
        //タイトルとボタンを追加する
        this.title = title
        dialogPane.buttonTypes.addAll(buttons)

        //FXMLファイルをロードして、コントローラ参照を取得する
        val loader = FXMLLoader(javaClass.getResource(form))
        dialogPane.content = loader.load()
        controller = loader.getController() as Controller

        //StyleSheetを登録
        dialogPane.stylesheets.add(javaClass.getResource("/Gview.css").toExternalForm())

        // "X"で閉じないようにする
        dialogPane.scene.window.onCloseRequest = EventHandler { it.consume() }
    }

    /**
     *  ボタンにプロパティ、ハンドラを関連付ける<br>
     *  ボタンは @see ButtonType で指定する
     *
     *  @param buttonType   対象にするボタン
     *  @param disable      Disabledプロパティを関連付ける
     *  @param handler      ボタン押下ハンドラを関連付ける
     */
    fun addButtonHandler(
            buttonType: ButtonType,
            disable: BooleanProperty?,
            handler: EventHandler<ActionEvent>? = null) {

        val button = dialogPane.lookupButton(buttonType)
        if(button != null) {
            if (disable != null) { button.disableProperty().bind(disable) }
            if (handler != null) { button.addEventFilter(ActionEvent.ACTION, handler) }
        }
    }

    /**
     *  ダイアログをモーダル表示する
     *
     *  @return OK/NGなどで終了した場合、そのButtonTypeが返される
     */
    override fun showDialog(): ButtonType? {
        val result = super.showAndWait()
        return if(result.isPresent) result.get() else null
    }
}
