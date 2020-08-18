package gview.gui.main

import gview.conf.Configuration
import gview.gui.branchlist.BranchList
import gview.gui.commitinfo.CommitInfo
import gview.gui.commitlist.CommitList
import gview.gui.framework.GviewBasePane
import gview.gui.framework.GviewBasePaneCtrl
import gview.model.GviewRepositoryModel
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXML
import javafx.scene.control.SplitPane
import javafx.scene.layout.AnchorPane

object MainWindow: GviewBasePane<MainWindowCtrl>(
        "/view/MainView.fxml",
        "MainWindow")

class MainWindowCtrl : GviewBasePaneCtrl() {

    @FXML private lateinit var mainSplit: SplitPane
    @FXML private lateinit var menuBar: AnchorPane
    @FXML private lateinit var branchList: AnchorPane
    @FXML private lateinit var commitList: AnchorPane
    @FXML private lateinit var commitInfo: AnchorPane
    @FXML private lateinit var statusBar: AnchorPane

    //リポジトリ
    val repository = GviewRepositoryModel()

    //SplitPaneのDivider位置を保持するProperty
    private val splitPositionsProperty = SimpleObjectProperty<DoubleArray>()
    private val splitPositions: DoubleArray get() { return splitPositionsProperty.value }

    //初期化処理
    fun initialize() {
        //Dividerの初期設定
        splitPositionsProperty.value = Configuration.systemModal.mainSplitPosProperty.value
        mainSplit.setDividerPositions(splitPositions[0], splitPositions[1])

        //Divider移動時にsplitPositionPropertyを更新する
        mainSplit.dividers[0].positionProperty().addListener { _, _, value
            -> splitPositions[0] = value.toDouble() }
        mainSplit.dividers[1].positionProperty().addListener { _, _, value
            -> splitPositions[1] = value.toDouble() }

        //Configuration情報にsplitPositionsPropertyをbind
        Configuration.systemModal.mainSplitPosProperty.bind(splitPositionsProperty)

        //初期化
        branchList.children.add(BranchList.root)
        commitList.children.add(CommitList.root)
        commitInfo.children.add(CommitInfo.root)
        menuBar.children.add(MenuBar.root)
        statusBar.children.add(StatusBar.root)
    }

}