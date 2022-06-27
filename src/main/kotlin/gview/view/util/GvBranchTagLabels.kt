package gview.view.util

import gview.model.commit.GvCommit
import javafx.scene.control.Label

/**
 * Branch/Tag名称表示ラベル
 *
 * @param model     表示するCommit情報
 */
class GvBranchTagLabels(model: GvCommit): ArrayList<Label>() {

    init {
        // ローカルブランチ、リモートブランチ、タグの順で表示する

        //ローカルブランチ
        model.localBranches.forEach {
            val label = Label(it.name)
            label.style = CSS.localBranchLabelStyle
            add(label)
        }
        //リモートブランチ
        model.remoteBranches.forEach {
            val label = Label("remote/${it.name}")
            label.style = CSS.remoteBranchLabelStyle
            add(label)
        }
        //タグ
        model.tags.forEach {
            val label = Label(it)
            label.style = CSS.tagLabelStyle
            add(label)
        }
    }

    object CSS {
        private val labelStyle = """
            -fx-font-size: 0.8em;
            -fx-font-weight: bold;
            -fx-padding: 0 5 0 5;
            -fx-background-insets: 0 2 0 0;
            -fx-border-style: solid;
            -fx-border-color: rgb(80,80,80);
            -fx-border-width: 2;
            -fx-border-radius: 2;
            -fx-border-insets: 0 2 0 0;
        """.trimIndent()

        val headLabelStyle = labelStyle + """
            -fx-background-color: rgb(207, 77, 77); 
        """.trimIndent()

        val localBranchLabelStyle = labelStyle + """
            -fx-background-color: rgb(117, 207, 77); 
        """.trimIndent()

        val remoteBranchLabelStyle = labelStyle + """
            -fx-background-color: rgb(206, 141, 128);
        """.trimIndent()

        val tagLabelStyle = labelStyle + """
            -fx-background-color: rgb(238, 238, 148);
        """.trimIndent()
    }
}
