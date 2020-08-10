package gview.gui.commitinfo

import gview.gui.commitlist.CommitListCtrl
import gview.gui.commitlist.CommitListView
import gview.gui.commitlist.CommitRowData
import gview.gui.commitlist.HeaderRowData
import gview.gui.framework.BaseCtrl
import javafx.fxml.FXML
import javafx.scene.control.SplitPane
import javafx.scene.layout.AnchorPane

class CommitInfoCtrl: BaseCtrl() {

    @FXML private lateinit var commitInfoPane: SplitPane
    @FXML private lateinit var commitInfoFiles: AnchorPane
    @FXML private lateinit var commitInfoDiff: AnchorPane

    private val commitFileListView = CommitFileListView.root
    private val commitDiffView = CommitDiffView.root

    //初期化
    fun initialize() {
    }

    //表示完了時にListenerを設定する
    override fun displayCompleted() {
        var commitList: CommitListCtrl = CommitListView.controller
        commitList.selectedRowProperty.addListener { _, _, newValue ->
            when(newValue) {
                is HeaderRowData -> selectHeaderRowData(newValue)
                is CommitRowData -> selectCommitRowData(newValue)
                else -> commitInfoPane.isVisible = false
            }
        }

        //初期状態は非表示
        commitInfoPane.isVisible = false
    }

    private fun selectHeaderRowData(data: HeaderRowData) {
        println("Header Selected")
    }

    private fun selectCommitRowData(data: CommitRowData) {
        commitInfoFiles.children.setAll(commitFileListView)
        commitInfoDiff.children.setAll(commitDiffView)
        CommitFileListView.controller.update(data.model)
        commitInfoPane.isVisible = true
        commitDiffView.isVisible = false
    }

}