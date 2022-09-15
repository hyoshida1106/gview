package gview.view.framework

import gview.resourceBundle
import javafx.beans.property.BooleanProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog

/**
 * カスタムダイアログ
 *
 * @param title         ダイアログのタイトル文字列
 * @param controller    コントローラインスタンス
 * @param buttons       表示するボタン
 */
open class GvCustomDialog<Controller>(title: String, val controller: Controller, vararg buttons: ButtonType)
    : Dialog<ButtonType>(), GvDialogInterface<ButtonType?> where Controller : GvCustomDialogCtrl {

    /**
     * 継承クラスの名称
     */
    private val className: String = javaClass.name.substringAfterLast(".")

    /**
     * 初期化
     */
    init {
        val formPath = "/dialog/${className}.fxml"              //NON-NLS
        val cssPath  = "/dialog/${className}.css"               //NON-NLS

        //タイトルとボタンを追加する
        this.title = title
        dialogPane.buttonTypes.addAll(buttons)

        //FXMLファイルをロードして、コントローラ参照を設定する
        val loader = FXMLLoader(javaClass.getResource(formPath), resourceBundle())
        loader.setController(controller)
        dialogPane.content = loader.load()

        //StyleSheetを登録
        dialogPane.stylesheets.add(javaClass.getResource("/Gview.css").toExternalForm())        //NON-NLS
        val localCSS = javaClass.getResource(cssPath)
        if(localCSS != null) {
            dialogPane.stylesheets.add(localCSS.toExternalForm())
        }
        dialogPane.styleClass.add("GvCustomDialog")                                             //NON-NLS
        dialogPane.content.styleClass.add(className)

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
    protected fun addButtonHandler(
        buttonType: ButtonType,
        disable: BooleanProperty?,
        handler: EventHandler<ActionEvent>? = null
    ) {
        val button = dialogPane.lookupButton(buttonType) ?: return
        if (disable != null) {
            button.disableProperty().bind(disable)
        }
        if (handler != null) {
            button.addEventFilter(ActionEvent.ACTION, handler)
        }
    }

    /**
     *  ダイアログをモーダル表示する
     *
     *  @return OK/NGなどで終了した場合、そのButtonTypeが返される
     */
    override fun showDialog(): ButtonType? {
        val result = super.showAndWait()
        return if (result.isPresent) result.get() else null
    }
}
