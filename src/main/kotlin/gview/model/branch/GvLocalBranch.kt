package gview.model.branch

import javafx.beans.property.SimpleBooleanProperty
import org.eclipse.jgit.lib.Ref
import java.lang.ref.WeakReference

class GvLocalBranch(branchList: GvBranchList, ref: Ref): GvBranch(branchList, ref) {

    //表示名
    override val name: String = branchList.localBranchDisplayName(ref.name)
    //パス
    override val path: String = ref.name
    //リモートブランチへのリンク
    var remoteBranch = WeakReference<GvRemoteBranch>(null)

    //カレントブランチフラグ
    val isCurrentBranch: Boolean get() = ( branchList.currentBranch.value == name )

    //表示対象フラグ
    val selectedFlagProperty = SimpleBooleanProperty(true)
}
