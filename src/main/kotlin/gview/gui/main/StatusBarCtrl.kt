package gview.gui.main

import gview.gui.framework.GviewBasePaneCtrl
import gview.model.GviewRepositoryModel
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.HBox

class StatusBarCtrl : GviewBasePaneCtrl() {

    @FXML private lateinit var statusBar: HBox
    @FXML private lateinit var repositoryPath: Label

    fun initialize() {
        statusBar.style = CSS.statusBarStyle
        repositoryPath.style = CSS.repogitoryPathStyle
    }

    //表示完了時にListenerを設定する
    override fun displayCompleted() {
        //リポジトリのパスを表示
        GviewRepositoryModel.currentRepository.addListener {
            repositoryPath.text = GviewRepositoryModel.currentRepository.jgitRepository?.directory?.absolutePath
        }
    }

    private object CSS {
        val statusBarStyle = """
            -fx-background-color: -background-color;
            -fx-effect: innershadow(three-pass-box, gray, 3, 0.5, 1, 1); 
        """.trimIndent()
        val  repogitoryPathStyle = """
            -fx-padding: 1 5 0 5; 
        """.trimIndent()
    }
}