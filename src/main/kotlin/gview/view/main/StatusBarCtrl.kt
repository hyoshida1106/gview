package gview.view.main

import gview.model.GvRepository
import gview.resourceBundle
import gview.view.framework.GvBaseWindowCtrl
import gview.view.util.GvTextMessage
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.layout.Pane

/**
 * ステータスバー
 */
class StatusBarCtrl: GvBaseWindowCtrl() {
    @FXML private lateinit var repositoryPath: Pane
    @FXML private lateinit var currentBranch: Pane

    /**
     * 初期化
     */
    fun initialize() {
        GvRepository.currentRepositoryProperty.addListener { _, _, repository
            -> Platform.runLater { updateRepository(repository) }
        }
    }

    /**
     * リポジトリ情報の更新
     */
    private fun updateRepository(repository: GvRepository?) {
        setCurrentRepositoryPath( repository?.absolutePath )
        setCurrentBranch( repository?.branches?.currentBranch?.value )
        repository?.branches?.currentBranch?.addListener { _, _, new ->
            Platform.runLater { setCurrentBranch( new ) } }
    }

    /**
     * リポジトリパスの表示変更
     */
    private fun setCurrentRepositoryPath(path: String?) {
        repositoryPath.children.setAll(
            GvTextMessage(resourceBundle().getString("CurrentRepository"), path ?: "" ))
    }

    /**
     * 選択中ブランチの表示変更
     */
    private fun setCurrentBranch(branchName: String?) {
        currentBranch.children.setAll(
            GvTextMessage(resourceBundle().getString("CurrentBranch"), branchName ?: "" ))
    }
}