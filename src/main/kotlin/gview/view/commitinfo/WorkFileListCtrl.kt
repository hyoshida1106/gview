package gview.view.commitinfo

import gview.conf.SystemModal
import gview.model.GvRepository
import gview.view.framework.GvBaseWindowCtrl
import gview.view.util.GvColumnAdjuster
import gview.model.commit.GvCommitFile
import gview.resourceBundle
import gview.view.function.WorkTreeFunction
import javafx.beans.property.SimpleIntegerProperty
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.AnchorPane

class WorkFileListCtrl: GvBaseWindowCtrl() {

    @FXML private lateinit var stageLabel: Label
    @FXML private lateinit var changeLabel: Label

    @FXML private lateinit var workFileListPane: SplitPane

    @FXML private lateinit var stagedFileList: TableView<RowData>
    @FXML private lateinit var stagedFileBottomBox: AnchorPane
    @FXML private lateinit var stagedFileTypeColumn: TableColumn<RowData, String>
    @FXML private lateinit var stagedFilePathColumn: TableColumn<RowData, String>

    @FXML private lateinit var changedFileList: TableView<RowData>
    @FXML private lateinit var changedFileBottomBox: AnchorPane
    @FXML private lateinit var changedFileTypeColumn: TableColumn<RowData, String>
    @FXML private lateinit var changedFilePathColumn: TableColumn<RowData, String>

    @FXML private lateinit var stageButton: Button
    @FXML private lateinit var unStageButton: Button
    @FXML private lateinit var commitButton: Button

    /* テーブルデータ */
    class RowData(val diffEntry: GvCommitFile) {
        val type: String = diffEntry.typeName
        val path: String = diffEntry.path
    }

    private val stagedFileNumber  = SimpleIntegerProperty(0)
    private val changedFileNumber = SimpleIntegerProperty(0)

    /* テーブルのカラム幅を調整する処理クラス */
    private lateinit var stagedFileListAdjuster: GvColumnAdjuster
    private lateinit var changedFileListAdjuster: GvColumnAdjuster

    //初期化
    fun initialize() {
        workFileListPane.setDividerPositions(SystemModal.workFileSplitPos.value)
        workFileListPane.dividers[0].positionProperty().addListener { _, _, value ->
            SystemModal.workFileSplitPos.value = value.toDouble()
        }
        stagedFileTypeColumn.cellValueFactory = PropertyValueFactory("type")
        stagedFilePathColumn.cellValueFactory = PropertyValueFactory("path")

        changedFileTypeColumn.cellValueFactory = PropertyValueFactory("type")
        changedFilePathColumn.cellValueFactory = PropertyValueFactory("path")

        stageLabel.text = resourceBundle().getString("Term.FileStage")
        stageLabel.styleClass.add("TitleClass")
        stagedFileBottomBox.styleClass.add("BottomBox")
        stagedFileTypeColumn.text = resourceBundle().getString("Term.FileType")
        stagedFileTypeColumn.styleClass.add("TypeColumn")
        stagedFilePathColumn.text = resourceBundle().getString("Term.FilePath")
        stagedFilePathColumn.styleClass.add("PathColumn")
        stagedFileList.placeholder = Label()

        changeLabel.text = resourceBundle().getString("Term.Update")
        changeLabel.styleClass.add("TitleClass")
        changedFileBottomBox.styleClass.add("BottomBox")
        changedFileTypeColumn.text = resourceBundle().getString("Term.FileType")
        changedFileTypeColumn.styleClass.add("TypeColumn")
        changedFilePathColumn.text = resourceBundle().getString("Term.FilePath")
        changedFilePathColumn.styleClass.add("PathColumn")
        changedFileList.placeholder = Label()

        commitButton.text = resourceBundle().getString("Term.Commit")
        unStageButton.text = resourceBundle().getString("Term.UnStage")
        stageButton.text = resourceBundle().getString("Term.FileStage")

        stagedFileListAdjuster = GvColumnAdjuster(stagedFileList, stagedFilePathColumn)
        changedFileListAdjuster = GvColumnAdjuster(changedFileList, changedFilePathColumn)

        stageButton.disableProperty().bind(changedFileNumber.isEqualTo(0))
        unStageButton.disableProperty().bind(stagedFileNumber.isEqualTo(0))
        commitButton.disableProperty().bind(stagedFileNumber.isEqualTo(0))

        commitButton.setOnAction    { WorkTreeFunction.doCommit() }
        unStageButton.setOnAction   { WorkTreeFunction.doUnStage() }
        stageButton.setOnAction     { WorkTreeFunction.doStage() }
    }

    //表示完了時にListenerを設定する
    override fun displayCompleted() {
        GvRepository.currentRepositoryProperty.addListener { _, _, repository ->
            javafx.application.Platform.runLater { updateRepository(repository) }
        }
        stagedFileList.selectionModel.selectedItemProperty().addListener { _, _, entry ->
            CommitDiff.controller.selectDiffEntry(entry?.diffEntry)
        }
        changedFileList.selectionModel.selectedItemProperty().addListener { _, _, entry ->
            CommitDiff.controller.selectDiffEntry(entry?.diffEntry)
        }

        stagedFileList.focusedProperty().addListener { _, _, newValue ->
            if (!newValue) stagedFileList.selectionModel.clearSelection()
        }
        changedFileList.focusedProperty().addListener { _, _, newValue ->
            if (!newValue) changedFileList.selectionModel.clearSelection()
        }

        stagedFileListAdjuster.adjustColumnWidth()
        changedFileListAdjuster.adjustColumnWidth()
    }

    private fun updateRepository(repository: GvRepository) {
        updateStagedFiles(repository.workFiles.stagedFiles.value)
        updateChangedFiles(repository.workFiles.changedFiles.value)
        repository.workFiles.stagedFiles.addListener  { _, _, new -> updateStagedFiles(new)  }
        repository.workFiles.changedFiles.addListener { _, _, new -> updateChangedFiles(new) }
    }

    private fun updateStagedFiles(files: List<GvCommitFile>?) {
        if(files != null) {
            stagedFileList.items.setAll(files.map { RowData(it) })
        } else {
            stagedFileList.items.clear()
        }
        stagedFileNumber.value = stagedFileList.items.size
    }

    private fun updateChangedFiles(files: List<GvCommitFile>?) {
        if(files != null) {
            changedFileList.items.setAll(files.map { RowData(it) })
        } else {
            changedFileList.items.clear()
        }
        changedFileNumber.value = changedFileList.items.size
    }
}
