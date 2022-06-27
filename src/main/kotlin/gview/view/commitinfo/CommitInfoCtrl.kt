package gview.view.commitinfo

import gview.view.commitlist.CommitList
import gview.view.commitlist.CommitListCtrl
import gview.view.commitlist.CommitRowData
import gview.view.commitlist.HeaderRowData
import gview.view.framework.GvBaseWindowCtrl
import gview.model.commit.GvCommit
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXML
import javafx.scene.control.SplitPane
import javafx.scene.layout.AnchorPane

class CommitInfoCtrl
    : GvBaseWindowCtrl() {

    @FXML private lateinit var commitInfoPane: SplitPane
    @FXML private lateinit var commitInfoFiles: AnchorPane
    @FXML private lateinit var commitInfoDiff: AnchorPane

    val commitDataProperty = SimpleObjectProperty<GvCommit?>()

    private val commitFileListView = CommitFileList.root
    private val headerFileListView = WorkFileList.root
    private val commitDiffView = CommitDiff.root

    //初期化
    fun initialize() {
    }

    //表示完了時にListenerを設定する
    override fun displayCompleted() {
        var commitList: CommitListCtrl = CommitList.controller
        commitList.selectedRowProperty.addListener { _, _, newValue ->
            when(newValue) {
                is HeaderRowData -> selectHeaderRowData()
                is CommitRowData -> selectCommitRowData(newValue)
                else -> commitInfoPane.isVisible = false
            }
        }

        //初期状態は非表示
        commitInfoPane.isVisible = false
    }

    private fun selectHeaderRowData() {
        commitInfoFiles.children.setAll(headerFileListView)
        commitInfoDiff.children.setAll(commitDiffView)
        commitDataProperty.value = null
        commitInfoPane.isVisible = true
        commitDiffView.isVisible = false
    }

    private fun selectCommitRowData(data: CommitRowData) {
        commitInfoFiles.children.setAll(commitFileListView)
        commitInfoDiff.children.setAll(commitDiffView)
        commitDataProperty.value = data.model
        commitInfoPane.isVisible = true
        commitDiffView.isVisible = false
    }

}
