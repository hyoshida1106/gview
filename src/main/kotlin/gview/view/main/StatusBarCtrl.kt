package gview.view.main

import gview.GvApplication
import gview.view.framework.GvBaseWindowCtrl
import gview.view.util.GvTextMessage
import javafx.fxml.FXML
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane

class StatusBarCtrl
    : GvBaseWindowCtrl() {

    @FXML private lateinit var statusBar: HBox
    @FXML private lateinit var repositoryPath: Pane
    @FXML private lateinit var currentBranch: Pane

    fun initialize() {
        statusBar.style = CSS.statusBarStyle
    }

    //表示完了時にListenerを設定する
    override fun displayCompleted() {
        //リポジトリのパスを表示
        GvApplication.app.currentRepository.addListener {
            setCurrentRepositoryPath(if(it.isValid) it.getJgitRepository().directory.absolutePath else "")
            setCurrentBranch( it.branches.currentBranch )
            it.branches.addListener { branches -> setCurrentBranch( branches.currentBranch ) }
        }
    }

    private fun setCurrentRepositoryPath(path: String?) {
        repositoryPath.children.setAll(GvTextMessage("Current Repository:", path ?: "" ))
    }

    private fun setCurrentBranch(branchName: String?) {
        currentBranch.children.setAll(GvTextMessage("Current Branch:", branchName ?: "" ))
    }

    private object CSS {
        val statusBarStyle = """
            -fx-background-color: -background-color;
            -fx-effect: innerShadow(three-pass-box, gray, 3, 0.5, 1, 1); 
            -fx-padding: 3 10;
            -fx-spacing: 10;
        """.trimIndent()
    }
}