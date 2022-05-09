package gview.model.branch

import org.eclipse.jgit.lib.Ref
import java.lang.ref.WeakReference

class GvRemoteBranch(branchList: GvBranchList, ref: Ref): GvBranch(branchList, ref) {
    //表示名
    override val name: String = branchList.remoteBranchDisplayName(ref.name)
    //パス
    override val path: String = ref.name
    //ローカルブランチへのリンク
    var localBranch = WeakReference<GvLocalBranch>(null)
}