package gview.gui.util

import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region

//ラベル付きテキスト表示
class TextMessage(title:String, message:String): HBox() {

    init {
        val titleLabel   = Label(title)
        titleLabel.style = CSS.titleStringStyle
        titleLabel.minWidth = Region.USE_PREF_SIZE
        val messageLabel = Label(message)
        messageLabel.style = CSS.messageStringStyle
        HBox.setHgrow(titleLabel, Priority.NEVER)
        children.addAll(titleLabel, messageLabel)
    }

    object CSS {
        val titleStringStyle = """
            -fx-font-weight: bold;
            -fx-padding: 1;
        """.trimIndent()

        val messageStringStyle = """
            -fx-padding: 1; 
        """.trimIndent()
    }
}

