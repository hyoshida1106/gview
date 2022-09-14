package gview.view.menu

import javafx.scene.control.Menu
import javafx.scene.input.KeyCodeCombination
import org.kordamp.ikonli.javafx.FontIcon

class GvSubMenu(
    text: String,
    bold: Boolean = false,
    iconLiteral: String? = null,
    accelerator: KeyCodeCombination? = null,
    vararg subMenuList: GvMenuItem
) : Menu(text) {

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
        items.setAll(subMenuList.toList())
    }
}