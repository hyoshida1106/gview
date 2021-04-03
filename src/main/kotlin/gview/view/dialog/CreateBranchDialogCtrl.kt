package gview.view.dialog

import gview.GvApplication
import gview.view.framework.GvCustomDialogCtrl
import gview.model.branch.GviewLocalBranchModel
import gview.model.commit.GviewCommitDataModel
import javafx.beans.property.SimpleBooleanProperty
import javafx.fxml.FXML
import javafx.scene.control.*

class CreateBranchDialogCtrl
    : GvCustomDialogCtrl() {

    @FXML private lateinit var headRadio: RadioButton
    @FXML private lateinit var branchName: TextField
    @FXML private lateinit var branchRadio: RadioButton
    @FXML private lateinit var tagRadio: RadioButton
    @FXML private lateinit var branchList: ChoiceBox<String>
    @FXML private lateinit var tagList: ChoiceBox<String>
    @FXML private lateinit var checkout: CheckBox
    private val selectorGroup = ToggleGroup()

    private lateinit var branchMap: Map<String,GviewLocalBranchModel>
    private lateinit var tagMap: Map<String, GviewCommitDataModel>

    //OKボタンの無効を指示するプロパティ
    val btnOkDisable = SimpleBooleanProperty(true)

    //ブランチ名称
    val newBranchName get() = branchName.text.trim()

    //ブランチ指定方法
    enum class BranchStartPoint { FromHead, ByOtherBranch, ByCommit}
    val startPoint: BranchStartPoint get() {
        return when (selectorGroup.selectedToggle) {
            headRadio -> BranchStartPoint.FromHead
            branchRadio -> BranchStartPoint.ByOtherBranch
            else -> BranchStartPoint.ByCommit
        }
    }

    //選択されたブランチ
    val selectedBranch: GviewLocalBranchModel? get() = branchMap[branchList.selectionModel.selectedItem]

    //選択されたタグ
    val selected: GviewCommitDataModel? get() = tagMap[tagList.selectionModel.selectedItem]

    //チェックアウトフラグ
    val checkoutFlag: Boolean get() = checkout.isSelected

    //初期化
    override fun initialize() {
        headRadio.toggleGroup = selectorGroup
        branchRadio.toggleGroup = selectorGroup
        tagRadio.toggleGroup = selectorGroup
        headRadio.isSelected = true

        checkout.isSelected = true

        branchMap = GvApplication.app.currentRepository.branches.localBranches.map { it.name to it }.toMap()
        if(branchMap.isNotEmpty()) {
            branchList.items.addAll(branchMap.keys)
            branchList.selectionModel.select(0)
            branchList.disableProperty().bind(branchRadio.selectedProperty().not())
        } else {
            branchRadio.isDisable = true
            branchList.isDisable = true
        }

        tagMap = GvApplication.app.currentRepository.commits.commitTagMap
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