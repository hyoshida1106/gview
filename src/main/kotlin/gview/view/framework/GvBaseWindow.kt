package gview.view.framework

import javafx.fxml.FXMLLoader
import javafx.scene.Parent

open class GvBaseWindow<Controller>(formPath: String, controlClass: String)
        where Controller: GvBaseWindowCtrl {

    val root: Parent
    val controller: Controller

    init {
        val loader = FXMLLoader(javaClass.getResource(formPath))
        root = loader.load()
        root.styleClass.add(controlClass)
        controller = loader.getController() as Controller
    }
}
