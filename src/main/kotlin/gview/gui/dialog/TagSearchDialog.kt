package gview.gui.dialog

import gview.gui.framework.GviewDialog
import gview.gui.main.MainWindow
import javafx.scene.control.TextInputDialog

class TagSearchDialog
    : TextInputDialog(),
        GviewDialog<String?> {

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