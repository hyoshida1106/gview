package gview.gui.framework

import javafx.beans.property.BooleanProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog

open class GviewDialog<Controller>(title: String, form: String, vararg buttons: ButtonType): Dialog<ButtonType>()
    where Controller: GviewDialogController
{
    val controller: Controller

    init {
        this.title = title
        dialogPane.buttonTypes.addAll(buttons)
        val loader = FXMLLoader(javaClass.getResource(form))
        dialogPane.content = loader.load()
        controller = loader.getController() as Controller

        //StyleSheetを登録
        dialogPane.stylesheets.add(javaClass.getResource("/Gview.css").toExternalForm())

        // "X"で閉じないようにする
        dialogPane.scene.window.onCloseRequest = EventHandler { it.consume() }
    }

    fun addButtonHandler(buttonType: ButtonType, disable: BooleanProperty?,
                         handler: EventHandler<ActionEvent>? = null) {
        val button = dialogPane.lookupButton(buttonType)
        if(button != null) {
            if (disable != null) { button.disableProperty().bind(disable) }
            if (handler != null) { button.addEventFilter(ActionEvent.ACTION, handler) }
        }
    }

    fun showDialog(): ButtonType? {
        val result = super.showAndWait()
        return if(result.isPresent) result.get() else null
    }
}

abstract class GviewDialogController {
    //初期化
    abstract fun initialize()
}