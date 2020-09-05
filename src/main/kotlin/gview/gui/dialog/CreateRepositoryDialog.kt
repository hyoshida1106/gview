package gview.gui.dialog

import gview.gui.framework.GviewCommonDialog
import gview.gui.framework.GviewDialog
import gview.gui.framework.GviewDialogController
import gview.model.GviewRepositoryModel
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.concurrent.Task
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.stage.DirectoryChooser
import org.controlsfx.control.MaskerPane

class OpenRepositoryDialog(path: String): GviewDialog<OpenRepositoryDialogCtrl> (
        "オープンするリポジトリのパスを指定してください",
        "/dialog/OpenRepositoryDialog.fxml",
        ButtonType.OK, ButtonType.CANCEL) {

    init {
        controller.setInitialPath(path)
        addButtonHandler(ButtonType.OK, controller.btnOkDisable) { controller.onOk(this, it) }
        addButtonHandler(ButtonType.CANCEL, controller.btnCancelDisable, null)
    }
}

class OpenRepositoryDialogCtrl : GviewDialogController() {

    @FXML private lateinit var pane: GridPane
    @FXML private lateinit var directoryPath: TextField
    @FXML private lateinit var directorySel: Button
    @FXML private lateinit var maskerPane: MaskerPane

    val btnOkDisable = SimpleBooleanProperty(false)
    val btnCancelDisable = SimpleBooleanProperty(false)

    //初期化
    override fun initialize() {
        pane.style = CSS.paneStyle

        //ファイル設定(ローカル)
        directorySel.onAction = EventHandler {
            val chooser = DirectoryChooser()
            chooser.title = "ディレクトリ"
            val dir = chooser.showDialog((it.target as Node).scene.window)
            if (dir != null) { directoryPath.text = dir.absolutePath }
        }

        btnOkDisable.bind(directoryPath.textProperty().isEmpty)
    }

    fun setInitialPath(path: String) {
        directoryPath.text = path
    }

    //OK押下時の処理
    fun onOk(dialog: GviewDialog<OpenRepositoryDialogCtrl>, event:ActionEvent) {
        val path = directoryPath.text
        try {
            btnOkDisable.unbind()
            btnOkDisable.value = true
            btnCancelDisable.value = true
            dialog.dialogPane.cursor = Cursor.WAIT
            val r = object: Task<Int>() {
                override fun call(): Int {
                    GviewRepositoryModel.currentRepository.openExist(path)
                    Platform.runLater { dialog.close() }
                    return 0
                }
            }
            maskerPane.visibleProperty().bind(r.runningProperty())
            Thread(r).start()
        } catch (e: Exception) {
            GviewCommonDialog.errorDialog(e)
            dialog.close()
        }
        event.consume()
    }

    object CSS {
        val paneStyle = """
            -fx-padding: 0 0 0 30;
        """.trimIndent()
    }
}

