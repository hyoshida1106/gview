package gview.gui.dialog

import gview.gui.framework.GviewDialog
import gview.gui.framework.GviewDialogController
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.StringProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.stage.DirectoryChooser

class CloneRepositoryDialog(remotePath: String, localPath: String) : GviewDialog<CloneRepositoryDialogCtrl> (
        "取得するリポジトリのパス/URLと、作成するリポジトリのパスを指定してください",
        "/dialog/CloneRepositoryDialog.fxml",
        ButtonType.OK, ButtonType.CANCEL) {

    val remotePath: String get() = controller.remoteRepositoryPathProperty.value
    val localPath: String get() = controller.localDirectoryPathProperty.value
    val bareRepo: Boolean get() = controller.bareRepositoryProperty.value

    init {
        controller.setInitialPath(remotePath, localPath)
        addButtonHandler(ButtonType.OK, controller.btnOkDisable)
    }
}

class CloneRepositoryDialogCtrl : GviewDialogController() {

    @FXML private lateinit var pane: GridPane
    @FXML private lateinit var remoteRepositoryPath: TextField
    @FXML private lateinit var localDirectoryPath: TextField
    @FXML private lateinit var remoteRepositorySel: Button
    @FXML private lateinit var localDirectorySel: Button
    @FXML private lateinit var bareRepository: CheckBox

    lateinit var remoteRepositoryPathProperty: StringProperty
    lateinit var localDirectoryPathProperty: StringProperty
    lateinit var bareRepositoryProperty: BooleanProperty
    val btnOkDisable = SimpleBooleanProperty(false)

    //初期化
    override fun initialize() {
        pane.style = CSS.paneStyle

        remoteRepositoryPathProperty = remoteRepositoryPath.textProperty()
        localDirectoryPathProperty = localDirectoryPath.textProperty()
        bareRepositoryProperty = bareRepository.selectedProperty()

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

    object CSS {
        val paneStyle = """
            -fx-padding: 0 0 0 30;
        """.trimIndent()
    }
}

