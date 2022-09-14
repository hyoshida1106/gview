package gview.view.dialog

import gview.resourceBundle
import gview.view.framework.GvDialogInterface
import gview.view.window.MainWindow
import javafx.scene.control.TextInputDialog

class TagSearchDialog : TextInputDialog(), GvDialogInterface<String?> {

    init {
        initOwner(MainWindow.root.scene.window)
        title = resourceBundle().getString("TagSearchDialog.Title")
        graphic = null
        headerText = null
        contentText = resourceBundle().getString("TagSearchDialog.Content")
    }

    override fun showDialog(): String? {
        val result = showAndWait()
        return if (result.isPresent) result.get() else null
    }
}