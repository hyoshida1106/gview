package gview.gui.commitinfo

import gview.gui.framework.BaseCtrl
import gview.gui.util.branchTagLabels
import gview.gui.util.textMessage
import gview.gui.util.verticalScrollBar
import gview.model.commit.GviewCommitDataModel
import gview.model.commit.GviewGitFileEntryModel
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXML
import javafx.scene.control.ScrollBar
import javafx.scene.control.SplitPane
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.text.TextFlow

class CommitFileListCtrl: BaseCtrl()  {

    @FXML private lateinit var commitFileListPane: SplitPane
    @FXML private lateinit var commitFileInfo: BorderPane
    @FXML private lateinit var commitFileListA: AnchorPane
    @FXML private lateinit var commitFileList: TableView<RowData>
    @FXML private lateinit var typeColumn: TableColumn<RowData, String>
    @FXML private lateinit var pathColumn: TableColumn<RowData, String>

    //選択中のDiff Entry
    val selectedCommitEntryProperty = SimpleObjectProperty<GviewGitFileEntryModel?>()

    /* テーブルデータ */
    class RowData(val diffEntry: GviewGitFileEntryModel) {
        val type: String = diffEntry.typeName
        val path: String = diffEntry.path
    }

    /* 縦スクロールバー */
    private var verticalScrollBar: ScrollBar? = null

    //初期化
    fun initialize() {
        typeColumn.cellValueFactory = PropertyValueFactory<RowData, String>("type")
        pathColumn.cellValueFactory = PropertyValueFactory<RowData, String>("path")
        commitFileList.style = CSS.fileListStyle
        typeColumn.style = CSS.typeColumnStyle
        pathColumn.style = CSS.pathColumnStyle
    }

    //表示完了時にListenerを設定する
    override fun displayCompleted() {

        //テーブル幅変更時のカラム幅調整
        commitFileList.widthProperty().addListener { _ -> adjustLastColumnWidth() }

        //Typeカラム幅変更時のカラム幅調整
        typeColumn.widthProperty().addListener { _ -> adjustLastColumnWidth() }
        pathColumn.widthProperty().addListener { _ -> adjustLastColumnWidth() }

        commitFileList.selectionModel.selectedItemProperty().addListener { _, _, entry ->
            selectedCommitEntryProperty.value = entry?.diffEntry
        }
    }

    fun update(model: GviewCommitDataModel) {
        updateFileInfo(model)
        updateFileList(model)
        selectedCommitEntryProperty.value = null
    }

    private fun updateFileInfo(model: GviewCommitDataModel) {

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

        commitFileInfo.children.clear()
        commitFileInfo.top = itemList
        commitFileInfo.center = commitMessage
    }

    private fun updateFileList(model: GviewCommitDataModel) {
        commitFileList.items.setAll(model.diffEntries.map { RowData(it) })
    }

    /* 縦スクロールバー表示の有無を確認した上で、カラム幅を決定する */
    private fun adjustLastColumnWidth() {
        val left = commitFileList.snappedLeftInset().toInt()
        val right = commitFileList.snappedRightInset().toInt()
        var width = commitFileList.width - typeColumn.width - left - right

        if(verticalScrollBar == null) {
            verticalScrollBar = verticalScrollBar(commitFileList)
            verticalScrollBar?.widthProperty()?.addListener { _ -> adjustLastColumnWidth() }
            verticalScrollBar?.visibleProperty()?.addListener { _ -> adjustLastColumnWidth() }
        }

        if(verticalScrollBar != null && verticalScrollBar!!.isVisible) {
            width -= verticalScrollBar!!.width
        }

        pathColumn.prefWidth = width
        pathColumn.minWidth  = width
        pathColumn.maxWidth  = width
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
            -fx-background-color: rgb(220,220,220);
            -fx-background-radius: 5;
        """.trimIndent()

        val fileListStyle = """
            -fx-padding: 10 8 7 8;
            -fx-background-color: rgb(220,220,220);
        """.trimIndent()

        val typeColumnStyle = """
            preWidth: 100;
            -fx-alignment: CENTER;
        """.trimIndent()

        val pathColumnStyle = """
        """.trimIndent()
    }
}