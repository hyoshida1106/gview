package gview

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import kotlin.system.exitProcess
import gview.gui.MainView
import gview.gui.framework.BaseCtrl
import gview.gui.util.IdleMonitor

/*
    Main Class
 */
class GViewApp : Application() {

    private lateinit var mainStage: Stage

    // アプリケーション起動
    override fun start(stage: Stage) {
        try {
            //Main Windowsのセットアップ
            mainStage = stage
            mainStage.title = "G/View"
            mainStage.scene = Scene(MainView.root, 1200.0, 900.0)
            mainStage.setOnShown { _ -> BaseCtrl.displayCompleted() }
            monitor.register(stage.scene)

            //Main Window表示
            stage.show()

        } catch(e: java.lang.Exception) {
            e.printStackTrace()
            exitProcess(-1)
        }
    }

    // 1秒のIDLE状態タイマ
    private val monitor = IdleMonitor(1000) {
        BaseCtrl.updateConfigInfo()
    }
}

// Main Function
fun main(args: Array<String>) {
    Application.launch(GViewApp::class.java, *args)
}