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

class CloneRepositoryDialog(remotePath: String, localPath: String) : GviewDialog<CloneRepositoryDialogCtrl> (
        "取得するリポジトリのパス/URLと、作成するリポジトリのパスを指定してください",
        "/dialog/CloneRepositoryDialog.fxml",
        ButtonType.OK, ButtonType.CANCEL) {

    init {
        controller.setInitialPath(remotePath, localPath)
        addButtonHandler(ButtonType.OK, controller.btnOkDisable) { controller.onOk(this, it) }
        addButtonHandler(ButtonType.CANCEL, controller.btnCancelDisable, null)
    }
}

class CloneRepositoryDialogCtrl : GviewDialogController() {

    @FXML private lateinit var pane: GridPane
    @FXML private lateinit var remoteRepositoryPath: TextField
    @FXML private lateinit var localDirectoryPath: TextField
    @FXML private lateinit var remoteRepositorySel: Button
    @FXML private lateinit var localDirectorySel: Button
    @FXML private lateinit var bareRepository: CheckBox
    @FXML private lateinit var maskerPane: MaskerPane

    val btnOkDisable = SimpleBooleanProperty(false)
    val btnCancelDisable = SimpleBooleanProperty(false)

    //初期化
    override fun initialize() {
        pane.style = CSS.paneStyle

        //ファイル設定(リモート)
        remoteRepositorySel.onAction = EventHandler {
            val chooser = DirectoryChooser()
            chooser.title = "リモートリポジトリ"
            val dir = chooser.showDialog((it.target as Node).scene.window)
            if (dir != null) { remoteRepositoryPath.text = dir.absolutePath }
        }

        //ファイル設定(ローカル)
        localDirectorySel.onAction = EventHandler {
            val chooser = DirectoryChooser()
            chooser.title = "ローカルディレクトリ"
            val dir = chooser.showDialog((it.target as Node).scene.window)
            if (dir != null) { localDirectoryPath.text = dir.absolutePath }
        }

        btnOkDisable.bind(remoteRepositoryPath.textProperty().isEmpty.or(localDirectoryPath.textProperty().isEmpty))
    }

    fun setInitialPath(remotePath: String, localPath: String) {
        remoteRepositoryPath.text = remotePath
        localDirectoryPath.text = localPath
    }

    //OK押下時の処理
    fun onOk(dialog: GviewDialog<CloneRepositoryDialogCtrl>, event:ActionEvent) {
        val remotePath = remoteRepositoryPath.text
        val localPath = localDirectoryPath.text
        val bareRepo = bareRepository.isSelected
        btnOkDisable.unbind()
        btnOkDisable.value = true
        btnCancelDisable.value = true
        dialog.dialogPane.cursor = Cursor.WAIT
        val r = object: Task<Int>() {
            override fun call(): Int {
                try {
                    GviewRepositoryModel.currentRepository.clone(localPath, remotePath, bareRepo)
                } catch (e: Exception) {
                    GviewCommonDialog.errorDialog(e)
                } finally {
                    Platform.runLater { dialog.close() }
                }
                return 0
            }
        }
        maskerPane.visibleProperty().bind(r.runningProperty())
        Thread(r).start()

        event.consume()
    }

    object CSS {
        val paneStyle = """
            -fx-padding: 0 0 0 30;
        """.trimIndent()
    }
}

