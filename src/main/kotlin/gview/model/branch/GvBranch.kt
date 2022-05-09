package gview.model.branch

import org.eclipse.jgit.lib.Ref

abstract class GvBranch(val branchList: GvBranchList, val ref: Ref) {
    //表示名
    abstract val name: String
    //パス
    abstract val path: String
}