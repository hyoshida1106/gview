package gview.model.branch

import gview.model.GviewBranchListModel
import gview.model.util.ModelObservable
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository

/*
    ローカルブランチ
 */
class GviewLocalBranchModel(val branchList: GviewBranchListModel, val ref: Ref):
        GviewBranchModel, ModelObservable<GviewLocalBranchModel>()  {

    //表示名
    override val name: String = Repository.shortenRefName(ref.name)

    //パス
    override val path: String = ref.name

    //リモートブランチへのリンク
    var remoteBranch: GviewRemoteBranchModel? = null

    //カレントリポジトリフラグ
    var isCurrentRepository: Boolean = (branchList.currentBranch == name)

    //表示対象フラグ
    private var selectedValue: Boolean = true
    var selected: Boolean
        get() {
            return selectedValue
        }
        set(it) {
            selectedValue = it
            fireCallback(this)
        }
}
