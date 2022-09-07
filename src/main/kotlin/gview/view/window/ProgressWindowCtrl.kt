package gview.view.window

import gview.model.GvProgressMonitor
import gview.view.framework.GvBaseWindowCtrl
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar

class ProgressWindowCtrl(private val monitor: GvProgressMonitor): GvBaseWindowCtrl() {
    @FXML private lateinit var message:Label
    @FXML private lateinit var progressBar: ProgressBar
    @FXML private lateinit var cancelButton: Button

    fun initialize() {
        monitor.titleProperty.addListener { _, _, _ -> updateMessage()  }
        monitor.scaleProperty.addListener { _, _, _ -> updateProgress() }
        monitor.valueProperty.addListener { _, _, _ -> updateProgress() }
        cancelButton.setOnAction { onCancel() }
    }

    private fun updateMessage() {
        Platform.runLater {
            message.text = monitor.title
        }
    }

    private fun updateProgress() {
        Platform.runLater {
            progressBar.progress = if (monitor.scale > 0) monitor.value.toDouble() / monitor.scale.toDouble() else 0.0
        }
    }

    private fun onCancel() {
        monitor.cancel = true
        cancelButton.isDisable = true
    }
}