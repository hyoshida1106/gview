package gview.gui.dialog

import gview.gui.framework.GviewDialog
import gview.gui.main.MainWindow
import javafx.scene.control.TextInputDialog

class BranchNameDialog
    : TextInputDialog(),
        GviewDialog<String?> {

    init {
        initOwner(MainWindow.root.scene.window)
        title = "ブランチ作成"
        graphic = null
        headerText = null
        contentText = "作成するブランチの名称"
    }

    override fun showDialog(): String? {
        val result = showAndWait()
        return if (result.isPresent) result.get() else null
    }
}