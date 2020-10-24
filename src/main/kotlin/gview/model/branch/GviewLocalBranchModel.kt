package gview.model.branch

import gview.model.util.ModelObservable
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository

class GviewLocalBranchModel(
        val branchList: GviewBranchListModel,
        val ref: Ref):
        GviewBranchModel,
        ModelObservable<GviewLocalBranchModel>() {

    //表示名
    override val name: String = Repository.shortenRefName(ref.name)

    //パス
    override val path: String = ref.name

    //リモートブランチへのリンク
    var remoteBranch: GviewRemoteBranchModel? = null

    //カレントブランチフラグ
    var isCurrentBranch: Boolean = (branchList.currentBranch == name)

    //表示対象フラグ
    var selected = true
        set(value) {
            if(field != value) {
                field = value
                fireCallback(this)
            }
        }
}
