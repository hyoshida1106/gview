package gview.view.main

import gview.model.GvRepository
import gview.view.framework.GvBaseWindowCtrl
import gview.view.util.GvTextMessage
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.layout.Pane

class StatusBarCtrl: GvBaseWindowCtrl() {

//  @FXML private lateinit var statusBar: HBox
    @FXML private lateinit var repositoryPath: Pane
    @FXML private lateinit var currentBranch: Pane

    fun initialize() {
        GvRepository.currentRepositoryProperty.addListener { _, _, repository
            -> Platform.runLater { updateRepository(repository) }
        }
    }

    private fun updateRepository(repository: GvRepository?) {
        setCurrentRepositoryPath( repository?.jgitRepository?.directory?.absolutePath )
        setCurrentBranch( repository?.branches?.currentBranch?.value )
        repository?.branches?.currentBranch?.addListener { _, _, new -> setCurrentBranch( new ) }
    }

    private fun setCurrentRepositoryPath(path: String?) {
        repositoryPath.children.setAll(GvTextMessage("Current Repository:", path ?: "" ))
    }

    private fun setCurrentBranch(branchName: String?) {
        currentBranch.children.setAll(GvTextMessage("Current Branch:", branchName ?: "" ))
    }
}