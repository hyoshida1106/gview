package gview

import gview.conf.SystemModal
import gview.view.dialog.ConfirmationDialog
import gview.view.dialog.ConfirmationDialog.ConfirmationType
import gview.view.main.MainWindow
import gview.view.framework.GvBaseWindowCtrl
import gview.view.util.GvIdleTimer
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.stage.Stage
import java.util.*
import kotlin.system.exitProcess

class GvApplication: Application() {

    private lateinit var mainStage: Stage

    override fun start(stage: Stage) {
        try {
            mainStage = stage
            mainStage.title = "GView"
            mainStage.scene = Scene(
                    MainWindow.root,
                    SystemModal.mainWidthProperty.value,
                    SystemModal.mainHeightProperty.value)
            mainStage.onShown = EventHandler {
                GvBaseWindowCtrl.displayCompleted()
            }
            mainStage.onCloseRequest = EventHandler {
                confirmToQuit()
                it.consume()
            }

            monitor.register(mainStage.scene)

            with(SystemModal) {
                mainHeightProperty.bind(mainStage.scene.heightProperty())
                mainWidthProperty.bind(mainStage.scene.widthProperty())
                maximumProperty.bind(mainStage.fullScreenProperty())
            }

            mainStage.show()

        } catch(e: java.lang.Exception) {
            e.printStackTrace()
            exitProcess(-1)
        }
    }

    private val monitor = GvIdleTimer(1000) {
        GvBaseWindowCtrl.updateConfigInfo()
        SystemModal.saveToFile()
    }

    companion object {
        fun confirmToQuit() {
            val message = resourceBundle().getString("QuitConformation")
            if (ConfirmationDialog(ConfirmationType.YesNo, message).showDialog()) {
                exitProcess(0)
            }
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(GvApplication::class.java, *args)
}

fun resourceBundle() = ResourceBundle.getBundle("Gview")