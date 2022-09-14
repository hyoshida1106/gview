package gview.view.commitinfo

import gview.view.framework.GvBaseWindowCtrl
import gview.view.util.GvCommitLabel
import gview.view.util.GvColumnAdjuster
import gview.view.util.GvTextMessage
import gview.model.commit.GvCommit
import gview.model.commit.GvCommitFile
import gview.resourceBundle
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.VBox
import javafx.scene.text.TextFlow

class CommitFileListCtrl: GvBaseWindowCtrl()  {
    @FXML private lateinit var commitProps: VBox
    @FXML private lateinit var commitMessage: TextArea
    @FXML private lateinit var commitFileList: TableView<RowData>
    @FXML private lateinit var typeColumn: TableColumn<RowData, String>
    @FXML private lateinit var pathColumn: TableColumn<RowData, String>

    /* テーブルデータ */
    class RowData(val diffEntry: GvCommitFile) {
        val type: String = diffEntry.typeName
        val path: String = diffEntry.path
    }

    /* テーブルのカラム幅を調整する処理クラス */
    private lateinit var commitFileListAdjuster: GvColumnAdjuster

    //初期化
    fun initialize() {
        typeColumn.cellValueFactory = PropertyValueFactory("type")
        pathColumn.cellValueFactory = PropertyValueFactory("path")
        typeColumn.styleClass.add("TypeColumn")
        pathColumn.styleClass.add("PathColumn")
        commitFileListAdjuster = GvColumnAdjuster(commitFileList, pathColumn)
    }

    //表示完了時にListenerを設定する
    override fun displayCompleted() {
        CommitInfo.controller.commitDataProperty.addListener { _, _, newValue -> update(newValue) }
        commitFileList.selectionModel.selectedItemProperty().addListener { _, _, entry ->
            CommitDiff.controller.selectDiffEntry(entry?.diffEntry)
        }
        commitFileListAdjuster.adjustColumnWidth()
    }

    private fun update(model: GvCommit?) {
        updateFileInfo(model)
        updateFileList(model)
        commitFileList.selectionModel.clearSelection()
    }

    private fun updateFileInfo(model: GvCommit?) {
        if(model != null) {
            val labelList = TextFlow()
            labelList.styleClass.add("LabelList")
            labelList.children.setAll(GvCommitLabel(model))

            commitProps.children.setAll(
                    GvTextMessage(resourceBundle().getString("Title.ID"), model.id.toString()),
                    GvTextMessage(resourceBundle().getString("Title.Date"), model.commitTime),
                    GvTextMessage(resourceBundle().getString("Title.Author"), model.author),
                    GvTextMessage(resourceBundle().getString("Title.Committer"), model.committer),
                    labelList)
            commitProps.styleClass.add("ItemList")
            commitMessage.text = model.fullMessage
        } else {
            commitProps.children.clear()
        }
    }

    private fun updateFileList(model: GvCommit?) {
        if(model != null) {
            commitFileList.items.setAll(model.diffEntries.map { RowData(it) })
        } else {
            commitFileList.items.clear()
        }
    }
}
