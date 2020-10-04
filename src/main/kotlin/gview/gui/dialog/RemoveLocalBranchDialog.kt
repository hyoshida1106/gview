package gview.gui.dialog

import gview.gui.framework.GviewDialog
import gview.gui.main.MainWindow
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority


class RemoveLocalBranchDialog(
        private val message: String,
        force: Boolean = false)
    : GviewDialog<Boolean> {

    private val forceCheckBox = CheckBox("強制的に削除")
    val forceRemove: Boolean get() = forceCheckBox.isSelected

    init {
        forceCheckBox.isSelected = force
    }

    override fun showDialog()
            : Boolean {
        val alert = Alert(Alert.AlertType.CONFIRMATION)
        val styleClass = alert.dialogPane.styleClass

        alert.initOwner(MainWindow.root.scene.window)
        alert.title = "Confirmation"
        alert.headerText = null
        alert.dialogPane = object : DialogPane() {
            override fun createButtonBar()
                    : Node {
                val checkBox = forceCheckBox
                val buttonBar = super.createButtonBar()
                HBox.setHgrow(checkBox, Priority.ALWAYS)
                HBox.setHgrow(buttonBar, Priority.NEVER)
                checkBox.maxWidth = Double.MAX_VALUE
                val hBox = HBox(5.0, checkBox, buttonBar)
                hBox.padding = Insets(10.0, 10.0, 10.0, 30.0)
                return hBox
            }
        }
        alert.dialogPane.contentText = message
        alert.dialogPane.buttonTypes.addAll(
                ButtonType("はい", ButtonBar.ButtonData.OK_DONE),
                ButtonType("いいえ", ButtonBar.ButtonData.CANCEL_CLOSE))
        alert.dialogPane.styleClass.setAll(styleClass)
        alert.dialogPane.applyCss()

        val result = alert.showAndWait()
        return result.isPresent && result.get().buttonData == ButtonBar.ButtonData.OK_DONE
    }
}