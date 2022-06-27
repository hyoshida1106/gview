package gview.model.branch

import javafx.beans.property.SimpleBooleanProperty
import org.eclipse.jgit.lib.Ref
import java.lang.ref.WeakReference

class GvLocalBranch(branchList: GvBranchList, ref: Ref): GvBranch(branchList, ref) {
    override val name: String = branchList.localBranchDisplayName(ref.name)
    override val path: String = ref.name

    var remoteBranch = WeakReference<GvRemoteBranch>(null)
    val isCurrentBranch: Boolean get() = ( branchList.currentBranch.value == name )
    val selectedFlagProperty = SimpleBooleanProperty(true)
}
