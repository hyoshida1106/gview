package gview.model.branch

import gview.model.GvRepository
import gview.view.dialog.ErrorDialog
import gview.view.main.MainWindow
import javafx.beans.property.SimpleBooleanProperty
import org.eclipse.jgit.lib.Ref
import java.lang.ref.WeakReference

/**
 *  ローカルブランチモデル
 *
 *  @constructor            プライマリコンストラクタ
 *  @param[branchList]      保持されるブランチリスト
 *  @param[ref]             ブランチ情報
 */
class GvLocalBranch(branchList: GvBranchList, ref: Ref) : GvBranch(branchList, ref) {
    /**
     * ローカルブランチ名称
     *
     * パスから取得する。
     */
    override val name: String = branchList.localBranchDisplayName(ref.name)

    /**
     * ローカルブランチのパス
     */
    override val path: String = ref.name

    /**
     * 関連付けられているリモートブランチの参照
     */
    var remoteBranch = WeakReference<GvRemoteBranch>(null)

    /**
     * 選択中のブランチである場合、trueを保持する
     */
    val isCurrentBranch get() = (branchList.currentBranch.value == name)

    /**
     * 表示対象ローカルブランチの場合、trueを保持する
     */
    val selectedFlagProperty = SimpleBooleanProperty(true)

    fun checkout() {
        repository.gitCommand.checkout()
            .setName(name)
            .call()
        repository.branchChanged()
    }

    fun pull() {
        repository.gitCommand.pull()
            .call()
        repository.branchChanged()
    }
}
