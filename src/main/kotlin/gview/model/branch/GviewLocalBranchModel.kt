package gview.model.branch

import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository

/*
    ローカルブランチ
 */
class GviewLocalBranchModel(val ref: Ref): GviewBranchModel {
    //表示名
    override val name: String = Repository.shortenRefName(ref.name)
    //パス
    override val path: String = ref.name
    //リモートブランチへのリンク
    var remoteBranch: GviewRemoteBranchModel? = null
}