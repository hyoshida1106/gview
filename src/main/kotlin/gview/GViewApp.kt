package gview

import gview.conf.SystemModal
import gview.gui.main.MainWindow
import gview.gui.framework.GviewBasePaneCtrl
import gview.gui.framework.GviewCommonDialog
import gview.gui.menu.FileMenu
import gview.gui.util.IdleMonitor
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.stage.Stage
import kotlin.system.exitProcess

/*
    Application Main Class
 */
class GViewApp : Application() {

    private lateinit var mainStage: Stage

    // アプリケーション起動
    override fun start(stage: Stage) {
        try {
            //Main Windowsのセットアップ
            mainStage = stage
            mainStage.title = "G/View"
            mainStage.scene = Scene(MainWindow.root,
                SystemModal.mainWidthProperty.value,
                SystemModal.mainHeightProperty.value)

            //表示完了イベントを各Paneに通知する
            mainStage.onShown = EventHandler {
                GviewBasePaneCtrl.displayCompleted()
            }

            //Windowが閉じられる場合の終了確認
            mainStage.onCloseRequest = EventHandler {
                if(GviewCommonDialog.confirmationDialog("アプリケーションを終了しますか？")) {
                    exitProcess(0)
                }
                it.consume()
            }

            //IDLE状態モニタ設定
            monitor.register(stage.scene)

            //Main WindowサイズをModal Informationにバインドする
            with(SystemModal) {
                mainHeightProperty.bind(mainStage.heightProperty())
                mainWidthProperty.bind(mainStage.widthProperty())
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
    Application.launch(GViewApp::class.java, *args)
}

