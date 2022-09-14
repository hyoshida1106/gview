package gview.view.menu

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.MenuItem
import javafx.scene.input.KeyCodeCombination
import org.kordamp.ikonli.javafx.FontIcon

/**
 * メニュー項目の基本クラス
 *
 *  @constructor            プライマリコンストラクタ
 *  @param[text]            メニュー名称
 *  @param[iconLiteral]     アイコン名称
 *  @param[accelerator]     アクセラレータ
 *  @param[eventHandler]    選択時に実行する処理
 */
class GvMenuItem(
    text: String,
    bold: Boolean = false,
    iconLiteral: String? = null,
    accelerator: KeyCodeCombination? = null,
    eventHandler: EventHandler<ActionEvent>
) : MenuItem(text) {

    private val boldStyle = "-fx-font-weight: bold;"           // NON-NLS

    init {
        if (bold) {
            style = boldStyle
        }
        if (iconLiteral != null) {
            graphic = FontIcon(iconLiteral)
        }
        if (accelerator != null) {
            this.accelerator = accelerator
        }
        onAction = eventHandler
    }

}
