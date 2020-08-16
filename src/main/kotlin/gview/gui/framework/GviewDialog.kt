package gview.gui.framework

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

        dialogPane.stylesheets.add(javaClass.getResource("/view/Gview.css").toExternalForm())
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