package gview.gui.main

import gview.conf.SystemModal
import gview.gui.branchlist.BranchList
import gview.gui.commitinfo.CommitInfo
import gview.gui.commitlist.CommitList
import gview.gui.framework.GviewBasePane
import gview.gui.framework.GviewBasePaneCtrl
import gview.gui.framework.GviewCommonDialog
import javafx.application.Platform
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.scene.Cursor
import javafx.scene.control.SplitPane
import javafx.scene.layout.AnchorPane
import org.controlsfx.control.MaskerPane

object MainWindow: GviewBasePane<MainWindowCtrl>(
        "/view/MainView.fxml",
        "MainWindow")

class MainWindowCtrl: GviewBasePaneCtrl() {

    @FXML private lateinit var mainSplit: SplitPane
    @FXML private lateinit var menuBar: AnchorPane
    @FXML private lateinit var branchList: AnchorPane
    @FXML private lateinit var commitList: AnchorPane
    @FXML private lateinit var commitInfo: AnchorPane
    @FXML private lateinit var statusBar: AnchorPane
    @FXML private lateinit var masker: MaskerPane

    val maskerVisibility: BooleanProperty get() = masker.visibleProperty()

    //SplitPaneのDivider位置を保持するProperty
    private val splitPositionsProperty = SimpleObjectProperty<DoubleArray>()
    private val splitPositions: DoubleArray get() { return splitPositionsProperty.value }

    //初期化処理
    fun initialize() {
        //Dividerの初期設定
        splitPositionsProperty.value = SystemModal.mainSplitPosProperty.value
        mainSplit.setDividerPositions(splitPositions[0], splitPositions[1])

        //Divider移動時にsplitPositionPropertyを更新する
        mainSplit.dividers[0].positionProperty().addListener { _, _, value
            -> splitPositions[0] = value.toDouble() }
        mainSplit.dividers[1].positionProperty().addListener { _, _, value
            -> splitPositions[1] = value.toDouble() }

        //Configuration情報にsplitPositionsPropertyをbind
        SystemModal.mainSplitPosProperty.bind(splitPositionsProperty)

        //初期化
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
                try { proc() }
                catch(e: Exception) { Platform.runLater { GviewCommonDialog.errorDialog(e) } }
                finally { Platform.runLater { scene.cursor = Cursor.DEFAULT } }
            }
        }
        masker.visibleProperty().bind(task.runningProperty())
        Thread(task).start()
    }

}