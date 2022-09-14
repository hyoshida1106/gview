package gview.view.util

import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region

/**
 * ラベル付きテキスト表示
 *
 * @param title         ラベル
 * @param message       メッセージ
 */
class GvTextMessage(title:String, message:String): HBox() {

    init {
        val localCSS = javaClass.getResource("/css/TextMessage.css")                 //NON-NLS
        if(localCSS != null) {
            stylesheets.add(localCSS.toExternalForm())
        }
        styleClass.add("TextMessage")               // NON-NLS

        val titleLabel   = Label(title)
        titleLabel.minWidth = Region.USE_PREF_SIZE
        titleLabel.styleClass.add("Title")          // NON-NLS
        children.add(titleLabel)

        val messageLabel = Label(message)
        setHgrow(titleLabel, Priority.NEVER)
        messageLabel.styleClass.add("Message")      // NON-NLS
        children.add(messageLabel)
    }
}

