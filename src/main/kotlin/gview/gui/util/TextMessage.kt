package gview.gui.util

import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region

//ラベル付きテキスト表示
fun textMessage(title:String, message:String): HBox {
    val titleLabel   = Label(title)
    titleLabel.style = titleStringStyle
    titleLabel.minWidth = Region.USE_PREF_SIZE
    val messageLabel = Label(message)
    messageLabel.style = messageStringStyle
    HBox.setHgrow(titleLabel, Priority.NEVER)
    return HBox(titleLabel, messageLabel)
}

private val titleStringStyle = """
    -fx-font-weight: bold;
    -fx-padding: 1;
""".trimIndent()

private val messageStringStyle = """
    -fx-padding: 1; 
""".trimIndent()
