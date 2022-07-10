package gview.view.commitinfo

import gview.view.framework.GvBaseWindowCtrl
import gview.view.util.GvBranchTagLabels
import gview.view.util.GvColumnAdjuster
import gview.view.util.GvTextMessage
import gview.model.commit.GvCommit
import gview.model.commit.GvCommitFile
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
        typeColumn.cellValueFactory = PropertyValueFactory<RowData, String>("type")
        pathColumn.cellValueFactory = PropertyValueFactory<RowData, String>("path")
        commitFileList.style = CSS.fileListStyle
        typeColumn.style = CSS.typeColumnStyle
        pathColumn.style = CSS.pathColumnStyle
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

        commitProps.children.clear()

        if(model != null) {
            val labelList = TextFlow()
            labelList.children.setAll(GvBranchTagLabels(model))
            labelList.style = CSS.labelListStyle

            commitProps.children.setAll(
                    GvTextMessage("ID: ", model.id.toString()),
                    GvTextMessage("日付: ", model.commitTime),
                    GvTextMessage("作者: ", model.author),
                    GvTextMessage("登録: ", model.committer),
                    labelList)
            commitProps.style = CSS.itemListStyle

            commitMessage.text = model.fullMessage
            commitMessage.style = CSS.commitMessageStyle
        }
    }

    private fun updateFileList(model: GvCommit?) {
        if(model != null) {
            commitFileList.items.setAll(model.diffEntries.map { RowData(it) })
        } else {
            commitFileList.items.clear()
        }
    }

    object CSS {
        val labelListStyle = """
            -fx-padding: 0 2 0 0;
        """.trimIndent()

        val itemListStyle = """
            -fx-padding: 3 10 0 10;
        """.trimIndent()

        val commitMessageStyle = """
            -fx-padding: 10;
            -fx-background-insets: 7;
            -fx-background-radius: 5;
        """.trimIndent()

        val fileListStyle = """
            -fx-padding: 10 8 7 8;
            -fx-background-color: -background-color;
        """.trimIndent()

        val typeColumnStyle = """
            -fx-alignment: CENTER;
        """.trimIndent()

        val pathColumnStyle = """
        """.trimIndent()
    }
}
