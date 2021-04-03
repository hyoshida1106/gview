package gview.view.menu

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.MenuItem
import javafx.scene.input.KeyCodeCombination
import org.kordamp.ikonli.javafx.FontIcon


class GviewMenuItem(
        text: String,
        iconLiteral: String? = null,
        accelerator: KeyCodeCombination? = null,
        eventHandler: EventHandler<ActionEvent>)
    : MenuItem(text) {

    init {
        if(iconLiteral != null) {
            var icon = FontIcon()
            icon.iconLiteral = iconLiteral
            graphic = icon
        }

        if(accelerator != null) {
            this.accelerator = accelerator
        }

        onAction = eventHandler
    }
}

