package gview.model.branch

import org.eclipse.jgit.lib.Ref
import java.lang.ref.WeakReference

class GvRemoteBranch(branchList: GvBranchList, ref: Ref): GvBranch(branchList, ref) {
    override val name: String = branchList.remoteBranchDisplayName(ref.name)
    override val path: String = ref.name

    var localBranch = WeakReference<GvLocalBranch>(null)
}