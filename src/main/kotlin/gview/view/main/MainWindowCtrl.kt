package gview.view.main

import gview.conf.SystemModal
import gview.view.branchlist.BranchList
import gview.view.commitinfo.CommitInfo
import gview.view.commitlist.CommitList
import gview.view.dialog.ErrorDialog
import gview.view.framework.GvBaseWindowCtrl
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.scene.Cursor
import javafx.scene.control.SplitPane
import javafx.scene.layout.AnchorPane
import org.controlsfx.control.MaskerPane

/**
 * メインウィンドウコントロールクラス
 */
class MainWindowCtrl: GvBaseWindowCtrl() {

    /**
     * メインウィンドウのスプリットペイン
     */
    @FXML private lateinit var mainSplit: SplitPane

    /**
     * メニューバー
     */
    @FXML private lateinit var menuBar: AnchorPane

    /**
     * 「ブランチ一覧」ペイン
     */
    @FXML private lateinit var branchList: AnchorPane

    /**
     * 「コミット一覧」ペイン
     */
    @FXML private lateinit var commitList: AnchorPane

    /**
     * 「コミット情報」ペイン
     */
    @FXML private lateinit var commitInfo: AnchorPane

    /**
     * ステータスバー
     */
    @FXML private lateinit var statusBar: AnchorPane

    /**
     * 処理中表示を行うためのマスク
     */
    @FXML private lateinit var masker: MaskerPane

    /**
     * 初期化
     */
    fun initialize() {
        //保存済情報からスプリットの表示位置を読み出して再現する
        mainSplit.setDividerPositions(SystemModal.mainSplitPos[0], SystemModal.mainSplitPos[1])
        //スプリット位置が変更された場合に、位置情報を取得するためのイベント定義
        mainSplit.dividers[0].positionProperty().addListener { _, _, value
            ->
            SystemModal.mainSplitPos[0] = value.toDouble()
        }
        mainSplit.dividers[1].positionProperty().addListener { _, _, value
            ->
            SystemModal.mainSplitPos[1] = value.toDouble()
        }
        //各ペインにウィンドウのルートインスタンスを設定する
        branchList.children.add(BranchList.root)
        commitList.children.add(CommitList.root)
        commitInfo.children.add(CommitInfo.root)
        menuBar.children.add(MenuBar.root)
        statusBar.children.add(StatusBar.root)
    }

    /**
     * 処理の実行
     *
     * カーソルの表示を切り替えた後、指定された処理を実行する。
     * 処理終了後、カーソルを元に戻す。
     * @param[function]         実行する関数
     */
    fun runTask(function: () -> Unit) {
        val scene = MainWindow.root.scene
        scene.cursor = Cursor.WAIT
        val task = object : Task<Unit>() {
            override fun call() {
                try {
                    function()
                } catch (e: Exception) {
                    Platform.runLater { ErrorDialog(e).showDialog() }
                } finally {
                    Platform.runLater { scene.cursor = Cursor.DEFAULT }
                }
            }
        }
        masker.visibleProperty().bind(task.runningProperty())
        Thread(task).start()
    }
}