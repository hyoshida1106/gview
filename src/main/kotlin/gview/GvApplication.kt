package gview

import gview.conf.SystemModal
import gview.view.dialog.ConfirmationDialog
import gview.view.dialog.ConfirmationDialog.ConfirmationType
import gview.view.main.MainWindow
import gview.view.framework.GvBaseWindowCtrl
import gview.view.util.GvIdleTimer
import gview.model.GviewRepositoryModel
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.stage.Stage
import kotlin.system.exitProcess

/**
 * Gviewアプリケーションクラス
 */
class GvApplication: Application() {

    companion object {
        /**
         * アプリケーションインスタンス
         */
        lateinit var instance : GvApplication
    }

    /**
     * MainWindowのステージを保持する
     */
    private lateinit var mainStage: Stage

    /**
     * 現在のリポジトリ情報を保持する
     */
    val currentRepository = GviewRepositoryModel()

    /**
     * 起動時にインスタンスを設定する
     */
    init {
        instance = this
    }

    /**
     * アプリケーション起動
     */
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
                GvBaseWindowCtrl.displayCompleted()
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
            mainStage.show()

        } catch(e: java.lang.Exception) {
            e.printStackTrace()
            exitProcess(-1)
        }
    }

    /**
     * アプリ終了確認ダイアログ
     */
    fun confirmToQuit() {
        val message = "アプリケーションを終了しますか？"
        if (ConfirmationDialog(ConfirmationType.YesNo, message).showDialog()) {
            exitProcess(0)
        }
    }

    /**
     * 1秒のIDLE状態タイマ
     */
    private val monitor = GvIdleTimer(1000) {
        GvBaseWindowCtrl.updateConfigInfo()
        SystemModal.saveToFile()
    }
}
