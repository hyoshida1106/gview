package gview.gui.dialog

import gview.gui.framework.GviewCustomDialogCtrl
import gview.model.GviewRepositoryModel
import gview.model.branch.GviewLocalBranchModel
import gview.model.commit.GviewCommitDataModel
import javafx.beans.property.SimpleBooleanProperty
import javafx.fxml.FXML
import javafx.scene.control.ChoiceBox
import javafx.scene.control.RadioButton
import javafx.scene.control.TextField
import javafx.scene.control.ToggleGroup

class CreateBranchDialogCtrl
    : GviewCustomDialogCtrl() {

    @FXML private lateinit var headRadio: RadioButton
    @FXML private lateinit var branchName: TextField
    @FXML private lateinit var branchRadio: RadioButton
    @FXML private lateinit var tagRadio: RadioButton
    @FXML private lateinit var branchList: ChoiceBox<String>
    @FXML private lateinit var tagList: ChoiceBox<String>
    private val selectorGroup = ToggleGroup()

    private lateinit var branchMap: Map<String,GviewLocalBranchModel>
    private lateinit var tagMap: Map<String, GviewCommitDataModel>

    val btnOkDisable = SimpleBooleanProperty(true)

    //初期化
    override fun initialize() {
        headRadio.toggleGroup = selectorGroup
        branchRadio.toggleGroup = selectorGroup
        tagRadio.toggleGroup = selectorGroup
        headRadio.isSelected = true

        val branches = GviewRepositoryModel.currentRepository.branches

        branchMap = branches.localBranches.map { it.name to it }.toMap()
        if(branchMap.isNotEmpty()) {
            branchList.items.addAll(branchMap.keys)
            branchList.selectionModel.select(0)
            branchList.disableProperty().bind(branchRadio.selectedProperty().not())
        } else {
            branchRadio.isDisable = true
            branchList.isDisable = true
        }

        tagMap = branches.commits.commitTagMap
        if(tagMap.isNotEmpty()) {
            tagList.items.addAll(tagMap.keys)
            tagList.selectionModel.select(0)
            tagList.disableProperty().bind(tagRadio.selectedProperty().not())
        } else {
            tagRadio.isDisable = true
            tagList.isDisable = true
        }

        branchName.text = ""
        branchName.lengthProperty().addListener { _ -> btnOkDisable.value = branchName.text.isBlank() }
    }
}