package gview.view.dialog

import gview.view.framework.GvDialogInterface
import gview.view.window.MainWindow
import javafx.scene.control.TextInputDialog

class TagSearchDialog
    : TextInputDialog(),
        GvDialogInterface<String?> {

    init {
        initOwner(MainWindow.root.scene.window)
        title = "タグ検索"
        graphic = null
        headerText = null
        contentText = "検索するタグ"
    }

    override fun showDialog(): String? {
        val result = showAndWait()
        return if (result.isPresent) result.get() else null
    }
}