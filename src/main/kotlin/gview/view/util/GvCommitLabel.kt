package gview.view.util

import gview.model.commit.GvCommit
import javafx.scene.control.Label

/**
 * Branch/Tag名称表示ラベル
 *
 * @param model     表示するCommit情報
 */
class GvCommitLabel(model: GvCommit): ArrayList<Label>() {
    private val localCSS = javaClass.getResource("/css/CommitLabel.css")?.toExternalForm()   //NON-NLS

    inner class CommitLabel(text: String, style: String): Label(text) {
        init {
            if(localCSS != null) stylesheets.add(localCSS)
            styleClass.addAll("CommitLabel", style)                                                     //NON-NLS
        }
    }

    init {
        //ローカルブランチ
        addAll(model.localBranches.map { CommitLabel(it.name, "LocalBranchLabel") } )                   //NON-NLS
        //リモートブランチ
        addAll(model.remoteBranches.map { CommitLabel("remote/${it.name}", "RemoteBranchLabel") })      //NON-NLS
        //タグ
        addAll(model.tags.map { CommitLabel(it, "TagLabel") })                                          //NON-NLS
    }
}
