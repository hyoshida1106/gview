package gview.gui.commitinfo

import gview.gui.framework.BaseCtrl
import gview.gui.util.TableColumnAdjuster
import gview.gui.util.branchTagLabels
import gview.gui.util.textMessage
import gview.gui.util.verticalScrollBar
import gview.model.commit.GviewCommitDataModel
import gview.model.commit.GviewGitFileEntryModel
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXML
import javafx.scene.control.ScrollBar
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.text.TextFlow

class CommitFileListCtrl: BaseCtrl()  {

    @FXML private lateinit var commitFileInfo: BorderPane
    @FXML private lateinit var commitFileList: TableView<RowData>
    @FXML private lateinit var typeColumn: TableColumn<RowData, String>
    @FXML private lateinit var pathColumn: TableColumn<RowData, String>

    /* テーブルデータ */
    class RowData(val diffEntry: GviewGitFileEntryModel) {
        val type: String = diffEntry.typeName
        val path: String = diffEntry.path
    }

    /* テーブルのカラム幅を調整する処理クラス */
    private lateinit var commitFileListAdjuster: TableColumnAdjuster

    //初期化
    fun initialize() {
        typeColumn.cellValueFactory = PropertyValueFactory<RowData, String>("type")
        pathColumn.cellValueFactory = PropertyValueFactory<RowData, String>("path")
        commitFileList.style = CSS.fileListStyle
        typeColumn.style = CSS.typeColumnStyle
        pathColumn.style = CSS.pathColumnStyle
        commitFileListAdjuster = TableColumnAdjuster(commitFileList, pathColumn)
    }

    //表示完了時にListenerを設定する
    override fun displayCompleted() {

        CommitInfoView.controller.commitDataProperty.addListener { _, _, newValue -> update(newValue) }

        commitFileList.selectionModel.selectedItemProperty().addListener { _, _, entry ->
            CommitDiffView.controller.selectDiffEntry(entry?.diffEntry)
        }

        commitFileListAdjuster.adjustColumnWidth()
    }

    private fun update(model: GviewCommitDataModel?) {
        updateFileInfo(model)
        updateFileList(model)
        commitFileList.selectionModel.clearSelection()
    }

    private fun updateFileInfo(model: GviewCommitDataModel?) {

        commitFileInfo.children.clear()

        if(model != null) {
            val labelList = TextFlow()
            labelList.children.setAll(branchTagLabels(model))
            labelList.style = CSS.labelListStyle

            val itemList = VBox(textMessage("ID: ", model.id.toString()),
                    textMessage("日付: ", model.commitTime),
                    textMessage("作者: ", model.author),
                    textMessage("登録: ", model.committer),
                    labelList)
            itemList.style = CSS.itemListStyle

            val commitMessage = TextFlow(Text(model.fullMessage))
            commitMessage.style = CSS.commitMessageStyle

            commitFileInfo.top = itemList
            commitFileInfo.center = commitMessage
        }
    }

    private fun updateFileList(model: GviewCommitDataModel?) {
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
            -fx-font-family: "Meiryo UI", sans-serif;
            -fx-padding: 15 20 5 20;
            -fx-background-insets: 7;
            -fx-background-color: -split-pane-color;
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