package gview.gui.dialog

import gview.gui.framework.GviewCommonDialog
import gview.gui.framework.GviewDialog
import gview.gui.framework.GviewDialogController
import gview.model.GviewRepositoryModel
import javafx.application.Platform
import javafx.beans.property.StringProperty
import javafx.concurrent.Task
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.stage.DirectoryChooser
import org.controlsfx.control.MaskerPane
import javax.management.loading.ClassLoaderRepository

class CloneRepositoryDialog(remotePath: String, localPath: String) : GviewDialog<CloneRepositoryDialogCtrl> (
        "取得するリポジトリのパスと、作成するリポジトリのパスを指定してください",
        "/dialog/CloneRepositoryDialog.fxml",
        ButtonType.OK, ButtonType.CANCEL) {

    private val remoteRepositoryPathProperty = controller.remoteRepositoryPathProperty
    private val localDirectoryPathProperty   = controller.localDirectoryPathProperty

    init {
        remoteRepositoryPathProperty.value = remotePath
        localDirectoryPathProperty.value   = localPath

        val btnOk = dialogPane.lookupButton(ButtonType.OK)
        btnOk.disableProperty().bind(remoteRepositoryPathProperty.isEmpty.or(localDirectoryPathProperty.isEmpty))
        btnOk.addEventFilter(ActionEvent.ACTION) { event -> controller.onOk(this, event) }
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

    val remoteRepositoryPathProperty: StringProperty get() = remoteRepositoryPath.textProperty()
    val localDirectoryPathProperty: StringProperty get() = localDirectoryPath.textProperty()

    //初期化
    override fun initialize() {
        pane.style = UserNameDialogCtrl.CSS.paneStyle

        //ファイル設定(リモート)
        remoteRepositorySel.onAction = EventHandler {
            val chooser = DirectoryChooser()
            chooser.title = "リモートリポジトリ"
            val dir = chooser.showDialog((it.target as Node)?.scene.window)
            if (dir != null) { remoteRepositoryPath.text = dir.absolutePath }
        }

        //ファイル設定(ローカル)
        localDirectorySel.onAction = EventHandler {
            val chooser = DirectoryChooser()
            chooser.title = "ローカルディレクトリ"
            val dir = chooser.showDialog((it.target as Node)?.scene.window)
            if (dir != null) { localDirectoryPath.text = dir.absolutePath }
        }
    }

    //OK押下時の処理
    fun onOk(dialog: GviewDialog<CloneRepositoryDialogCtrl>, event:ActionEvent) {
        val remotePath = remoteRepositoryPath.text
        val localPath = localDirectoryPath.text
        val bareRepo = bareRepository.isSelected
        try {
            dialog.dialogPane.cursor = Cursor.WAIT
            val r = object: Task<Int>() {
                override fun call(): Int {
                    GviewRepositoryModel.currentRepository.clone(localPath, remotePath, bareRepo)
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

