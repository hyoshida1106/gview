package gview.model.branch

import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository

class GviewRemoteBranchModel(
        val branchList: GviewBranchListModel,
        val ref: Ref,
        repository: Repository)
    : GviewBranchModel {

    //表示名
    override val name: String = repository.shortenRemoteBranchName(ref.name)
    //パス
    override val path: String = ref.name
    //ローカルブランチへのリンク
    var localBranch: GviewLocalBranchModel? = null
}