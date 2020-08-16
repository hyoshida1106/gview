package gview

import gview.conf.Configuration
import gview.gui.MainView
import gview.gui.framework.GviewBasePaneCtrl
import gview.gui.util.IdleMonitor
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import kotlin.system.exitProcess

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
            mainStage.scene = Scene(MainView.root,
                Configuration.systemModal.mainWidth,
                Configuration.systemModal.mainHeight)
            mainStage.setOnShown { _ -> GviewBasePaneCtrl.displayCompleted() }
            //IDLE状態モニタ設定
            monitor.register(stage.scene)
            //Main WindowサイズをModal Informationにバインドする
            with(Configuration.systemModal) {
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
        Configuration.saveToFile()
    }
}

// Main Function
fun main(args: Array<String>) {
    Application.launch(GViewApp::class.java, *args)
}

