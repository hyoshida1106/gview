package gview.view.util

import gview.model.GvCommit
import javafx.scene.control.Label

/**
 * Branch/Tag名称表示ラベル
 *
 * @param model     表示するCommit情報
 */
class GvCommitLabel(model: GvCommit): ArrayList<Label>() {
    inner class CommitLabel(text: String, style: String): Label(text) {
        init {
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
