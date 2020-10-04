package gview.gui.dialog

import gview.gui.framework.GviewDialog
import gview.gui.main.MainWindow
import javafx.scene.control.TextInputDialog

class CommentSearchDialog()
    : TextInputDialog(),
        GviewDialog<String?> {

    init {
        initOwner(MainWindow.root.scene.window)
        title = "コメント検索"
        graphic = null
        headerText = null
        contentText = "検索するコメント文字列"
    }

    override fun showDialog()
            : String? {
        val result = showAndWait()
        return if (result.isPresent) result.get() else null
    }
}