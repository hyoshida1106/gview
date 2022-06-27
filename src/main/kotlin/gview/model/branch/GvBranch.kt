package gview.model.branch

import org.eclipse.jgit.lib.Ref

abstract class GvBranch(val branchList: GvBranchList, val ref: Ref) {
    abstract val name: String
    abstract val path: String
}