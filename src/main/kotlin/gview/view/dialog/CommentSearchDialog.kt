package gview.view.dialog

import gview.view.framework.GvDialogInterface
import gview.view.main.MainWindow
import javafx.scene.control.TextInputDialog

class CommentSearchDialog()
    : TextInputDialog(),
        GvDialogInterface<String?> {

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