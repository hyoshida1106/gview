package gview.gui.framework

import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.TextInputDialog
import java.util.Optional
import gview.gui.main.MainWindow

/*
    共通ダイアログを生成するユーティリティ
 */

object GviewCommonDialog {

    //JavaのOptionインスタンスをnullableに変換する
    private fun <T> optionToNullable(optionValue: Optional<T>): T? {
        return if(optionValue.isPresent) optionValue.get() else null
    }

    /**
        メッセージダイアログを表示して、ユーザ入力を待つ

        @param alertType    メッセージタイプを指定する
        @param message      ダイアログに表示するメッセージ文字列
        @return             押下されたボタン種別。内容はalertStyleに依存する。
     */
    private fun alertAndWait(alertType: Alert.AlertType, title: String, message: String?): ButtonType? {
        val alert = Alert(alertType)
        alert.initOwner(MainWindow.root.scene.window)
        alert.title = title
        alert.headerText = null
        alert.contentText = message
        return optionToNullable(alert.showAndWait())
    }

    /**
        例外通知ダイアログ

        @param e            例外インスタンス
     */
    fun createErrorDialog(e: Exception) {
        alertAndWait(Alert.AlertType.ERROR, "Exception", e.message)
    }

    /**
        エラーダイアログ

        @param message      表示するメッセージ
     */
    fun createErrorDialog(message: String) {
        alertAndWait(Alert.AlertType.ERROR, "Error", message)
    }

    /**
        確認ダイアログ (OK/Cancel)

        @param message      表示するメッセージ
        @return             OKならばtrue
     */

    fun createConfirmDialog(message: String): Boolean {
        return alertAndWait(Alert.AlertType.CONFIRMATION, "Confirmation", message) == ButtonType.OK
    }

    /**
        単純な文字列入力ダイアログ

        @param message      表示するメッセージ
        @param header       ヘッダメッセージ
        @return             入力文字列またはnull
     */

    fun createSimpleTextDialog(message: String, title: String?, header: String? = null): String? {
        val dialog = TextInputDialog()
        dialog.initOwner(MainWindow.root.scene.window)
        dialog.title = title
        dialog.headerText = header
        dialog.contentText = message
        return optionToNullable(dialog.showAndWait())
    }

}
