package gview.view.commitinfo

import gview.view.framework.GvBaseWindowCtrl
import gview.model.commit.GvCommitFile
import javafx.fxml.FXML
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.layout.BorderPane
import java.nio.charset.Charset

class CommitDiffCtrl: GvBaseWindowCtrl() {
    @FXML private lateinit var commitDiffViewPane: BorderPane
    @FXML private lateinit var diffList: ListView<String>

    //初期化
    fun initialize() {
        commitDiffViewPane.isVisible = false
        diffList.setCellFactory { DiffCell() }
    }

    fun selectDiffEntry(entry: GvCommitFile?) {
        if (entry != null) {
            diffList.items.setAll(
                entry.exportDiffText().inputStream().bufferedReader(Charset.forName("utf-8")).readLines()       //NON-NLS
            )
            commitDiffViewPane.isVisible = true
        } else {
            commitDiffViewPane.isVisible = false
        }
    }

    private class DiffCell : ListCell<String>() {
        override fun updateItem(text: String?, empty: Boolean) {
            super.updateItem(text, empty)
            if (text != null && !empty) {
                styleClass.setAll(
                    when {
                        isAddLine(text) -> "AddedLine"                    //NON-NLS
                        isDelLine(text) -> "DeletedLine"                  //NON-NLS
                        else -> "OtherLine"                               //NON-NLS
                    }
                )
                setText(text)
            } else {
                styleClass.clear()
                setText(null)
            }
        }
        private fun isAddLine(text: String): Boolean {
            return (!text.startsWith("+++") && text.startsWith("+"))      //NON-NLS
        }
        private fun isDelLine(text: String): Boolean {
            return (!text.startsWith("---") && text.startsWith("-"))      //NON-NLS
        }
    }
}
