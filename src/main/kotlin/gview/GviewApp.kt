package gview

import gview.conf.SystemModal
import gview.gui.dialog.ConfirmationDialog
import gview.gui.dialog.ConfirmationDialog.ConfirmationType
import gview.gui.main.MainWindow
import gview.gui.framework.GviewBasePaneCtrl
import gview.gui.util.IdleMonitor
import gview.model.GviewRepositoryModel
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.stage.Stage
import kotlin.system.exitProcess

/*
    Application Main Class
 */
class GviewApp: Application() {

    private lateinit var mainStage: Stage

    companion object {
        //リポジトリインスタンス、現状は１つのみ
        val currentRepository = GviewRepositoryModel()

        //アプリ終了の確認処理
        fun confirmToQuit() {
            val message = "アプリケーションを終了しますか？"
            if(ConfirmationDialog(ConfirmationType.YesNo, message).showDialog()) {
                exitProcess(0)
            }
        }
    }

    // アプリケーション起動
    override fun start(stage: Stage) {
        try {
            //Main Windowsのセットアップ
            mainStage = stage
            mainStage.title = "G/View"
            mainStage.scene = Scene(
                    MainWindow.root,
                    SystemModal.mainWidthProperty.value,
                    SystemModal.mainHeightProperty.value)

            //表示完了イベントを各Paneに通知する
            mainStage.onShown = EventHandler {
                GviewBasePaneCtrl.displayCompleted()
            }

            //Windowが閉じられる場合の終了確認
            mainStage.onCloseRequest = EventHandler {
                confirmToQuit()
                it.consume()
            }

            //IDLE状態モニタ設定
            monitor.register(stage.scene)

            //Main WindowサイズをModal Informationにバインドする
            with(SystemModal) {
                mainHeightProperty.bind(mainStage.scene.heightProperty())
                mainWidthProperty.bind(mainStage.scene.widthProperty())
                maximumProperty.bind(mainStage.fullScreenProperty())
            }

            //Main Window表示
            stage.show()

        } catch(e: java.lang.Exception) {
            e.printStackTrace()
            exitProcess(-1)
        }
    }

    // 1秒のIDLE状態タイマ
    private val monitor = IdleMonitor(1000) {
        GviewBasePaneCtrl.updateConfigInfo()
        SystemModal.saveToFile()
    }
}

// Main Function
fun main(args: Array<String>) {
    Application.launch(GviewApp::class.java, *args)
}

