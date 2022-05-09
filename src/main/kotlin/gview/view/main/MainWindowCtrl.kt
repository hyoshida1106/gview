package gview.view.main

import gview.conf.SystemModal
import gview.model.GvRepository
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

class MainWindowCtrl: GvBaseWindowCtrl() {
    @FXML private lateinit var mainSplit: SplitPane
    @FXML private lateinit var menuBar: AnchorPane
    @FXML private lateinit var branchList: AnchorPane
    @FXML private lateinit var commitList: AnchorPane
    @FXML private lateinit var commitInfo: AnchorPane
    @FXML private lateinit var statusBar: AnchorPane
    @FXML private lateinit var masker: MaskerPane

    //初期化処理
    fun initialize() {
        mainSplit.setDividerPositions(
            SystemModal.mainSplitPos[0], SystemModal.mainSplitPos[1])
        mainSplit.dividers[0].positionProperty().addListener { _, _, value
            -> SystemModal.mainSplitPos[0] = value.toDouble() }
        mainSplit.dividers[1].positionProperty().addListener { _, _, value
            -> SystemModal.mainSplitPos[1] = value.toDouble() }

        branchList.children.add(BranchList.root)
        commitList.children.add(CommitList.root)
        commitInfo.children.add(CommitInfo.root)
        menuBar.children.add(MenuBar.root)
        statusBar.children.add(StatusBar.root)
    }

    //「実行中」を表示して処理を行う
    fun runTask(proc: () -> Unit) {
        val scene = MainWindow.root.scene
        scene.cursor = Cursor.WAIT
        val task = object: Task<Unit>() {
            override fun call() {
                try {
                    proc()
                } catch(e: Exception) {
                    Platform.runLater { ErrorDialog(e).showDialog() }
                }
                finally { Platform.runLater { scene.cursor = Cursor.DEFAULT } }
            }
        }
        masker.visibleProperty().bind(task.runningProperty())
        Thread(task).start()
    }

}