package gview.gui.framework

import javafx.fxml.FXMLLoader
import javafx.scene.control.Menu

open class GviewBaseMenu<Controller>(form: String) {
    val menu: Menu               //親インスタンス参照
    val command: Controller      //コントローラ参照

    init {
        val loader = FXMLLoader(javaClass.getResource(form))
        menu = loader.load()
        command = loader.getController() as Controller
    }
}

