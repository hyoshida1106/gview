package gview.gui.util

import gview.model.commit.GviewCommitDataModel
import javafx.scene.control.Label

fun branchTagLabels(model: GviewCommitDataModel): List<Label> {

    val labels = mutableListOf<Label>()

    //ローカルブランチ
    model.localBranches.forEach {
        val label = Label(it.name)
        label.style = localBranchLabelStyle
        labels.add(label)
    }
    //リモートブランチ
    model.remoteBranches.forEach {
        val label = Label("remote/${it.name}")
        label.style = remoteBranchLabelStyle
        labels.add(label)
    }
    //タグ
    model.tags.forEach {
        val label = Label(it)
        label.style = tagLabelStyle
        labels.add(label)
    }

    return labels
}

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

private val localBranchLabelStyle = labelStyle + """
    -fx-background-color: rgb(117, 207, 77); 
""".trimIndent()

private val remoteBranchLabelStyle = labelStyle + """
    -fx-background-color: rgb(206, 141, 128);
""".trimIndent()

private val tagLabelStyle = labelStyle + """
    -fx-background-color: rgb(238, 238, 148);
""".trimIndent()

