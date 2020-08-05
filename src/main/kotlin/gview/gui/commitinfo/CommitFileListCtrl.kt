package gview.gui.commitinfo

import gview.gui.framework.BaseCtrl
import gview.gui.util.branchTagLabels
import gview.gui.util.textMessage
import gview.model.commit.GviewCommitDataModel
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.scene.text.TextFlow

class CommitFileListCtrl: BaseCtrl()  {

    @FXML private lateinit var commitFileListPane: SplitPane
    @FXML private lateinit var commitFileInfo: AnchorPane
    @FXML private lateinit var commitFileList: AnchorPane

    //初期化
    fun initialize() {
    }

    //表示完了時にListenerを設定する
    override fun displayCompleted() {
    }

    fun update(model: GviewCommitDataModel) {
        updateFileInfo(model)
        updateFileList(model)
    }

    private fun updateFileInfo(model: GviewCommitDataModel) {
        val commitMessage = Label(model.fullMessage)
        commitMessage.style = CSS.commitMessageStyle

        val labelList = TextFlow()
        labelList.children.setAll(branchTagLabels(model))

        commitFileInfo.children.clear()
        commitFileInfo.children.add(VBox(
                labelList,
                textMessage("ID: ", model.id.toString()),
                textMessage("Commit Time: ", model.commitTime),
                textMessage("Committer: ", model.committer),
                textMessage("Author: ", model.author),
                commitMessage))
    }

    private fun updateFileList(model: GviewCommitDataModel) {
    }

    object CSS {
        val commitMessageStyle = """
            -fx-font-size: 1.1em;
            -fx-wrap-text: true;
            -fx-padding: 5;
        """.trimIndent()
    }
}