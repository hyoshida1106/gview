package gview.gui.commitinfo

import gview.gui.framework.GviewBasePaneCtrl
import gview.model.commit.GviewGitFileEntryModel
import javafx.fxml.FXML
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.layout.BorderPane
import java.nio.charset.Charset

class CommitDiffCtrl: GviewBasePaneCtrl() {

    @FXML
    private lateinit var commitDiffViewPane: BorderPane
    @FXML
    private lateinit var diffList: ListView<String>

    //初期化
    fun initialize() {
        commitDiffViewPane.isVisible = false
        diffList.style = CSS.diffListStyle
        diffList.setCellFactory { DiffCell() }
    }

    fun selectDiffEntry(entry: GviewGitFileEntryModel?) {
        if(entry != null) {
            diffList.items.setAll(
                    entry.exportDiffText().inputStream().bufferedReader(Charset.forName("utf-8")).readLines())
            commitDiffViewPane.isVisible = true
        } else {
            commitDiffViewPane.isVisible = false
        }
    }

    private class DiffCell : ListCell<String>() {

        override fun updateItem(text: String?, empty: Boolean) {
            super.updateItem(text, empty)
            if (text != null && !empty) {
                style = when {
                    isAddLine(text) -> CSS.addLineStyle
                    isDelLine(text) -> CSS.delLineStyle
                    else -> ""
                }
                setText(text)
            } else {
                style = ""
                setText(null)
            }
        }

        private fun isAddLine(text: String): Boolean {
            return (!text.startsWith("+++") && text.startsWith("+"))
        }

        private fun isDelLine(text: String): Boolean {
            return (!text.startsWith("---") && text.startsWith("-"))
        }
    }

    private object CSS {
        val diffListStyle = """
            -fx-font-family: "monospace";
            -fx-background-color: rgb(220,220,220);
            -fx-padding: 10;
            -fx-background-insets: 7;
            -fx-background-radius: 5;
        """.trimIndent()
        val addLineStyle = """
            -fx-background-color: rgb(255, 180, 180); 
        """.trimIndent()
        val delLineStyle = """
            -fx-background-color: rgb(180, 180, 255);
        """.trimIndent()
    }
}
