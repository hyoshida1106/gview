package gview.model.branch

import javafx.beans.property.SimpleBooleanProperty
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
    //表示対象フラグ
    val selectedProperty = SimpleBooleanProperty(true)
    val selected: Boolean get() { return selectedProperty.value }
}