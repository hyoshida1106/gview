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

/**
 * アプリケーションクラス
 */
class GvApplication : Application() {

    /**
     * アプリケーション起動処理
     *
     * @param[stage]    メイン画面
     */
    override fun start(stage: Stage) {
        try {
            // タイトルとSceneを設定
            stage.title = "GView"
            stage.scene = Scene(
                MainWindow.root,
                SystemModal.mainWidthProperty.value,
                SystemModal.mainHeightProperty.value
            )
            // 表示完了イベントハンドラ定義
            stage.onShown = EventHandler {
                // 全ウィンドウの``displayComplete()``メソッドを呼び出す
                GvBaseWindowCtrl.displayCompleted()
            }
            // ウィンドウクローズ要求
            stage.onCloseRequest = EventHandler {
                // ダイアログを表示して、クローズ確認を行う
                confirmToQuit()
                it.consume()
            }
            //アイドルタイマの登録
            monitor.register(stage.scene)
            //画面サイズ変更時、サイズを保存するためのbind定義
            with(SystemModal) {
                mainHeightProperty.bind(stage.scene.heightProperty())
                mainWidthProperty.bind(stage.scene.widthProperty())
                maximumProperty.bind(stage.fullScreenProperty())
            }
            //メイン画面の表示
            stage.show()
        } catch (e: java.lang.Exception) {
            //例外発生時、StackTraceを表示して終了する
            e.printStackTrace()
            exitProcess(-1)
        }
    }

    /**
     * アイドルタイマインスタンス
     *
     * 画面操作が1秒間行われない場合、ハンドラが実行される。
     * 表示情報を[SystemModal]に記録した後、ファイルへの書き込みを行う。
     */
    private val monitor = GvIdleTimer(1000) {
        GvBaseWindowCtrl.updateConfigInfo()
        SystemModal.saveToFile()
    }

    /**
     * コンパニオンオブジェクト
     */
    companion object {
        /**
         * アプリケーションの終了確認
         *
         * ダイアログを表示し、OKであればプロセスを終了する
         */
        fun confirmToQuit() {
            val message = resourceBundle().getString("QuitConformation")
            if (ConfirmationDialog(ConfirmationType.YesNo, message).showDialog()) {
                exitProcess(0)
            }
        }
    }
}

/**
 * アプリケーションエントリ
 */
fun main(args: Array<String>) {
    Application.launch(GvApplication::class.java, *args)
}

/**
 * リソースバンドルを参照するためのグローバルインスタンス
 */
fun resourceBundle(): ResourceBundle = ResourceBundle.getBundle("Gview")