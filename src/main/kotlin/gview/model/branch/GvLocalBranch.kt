package gview.model.branch

import gview.model.GvProgressMonitor
import javafx.beans.property.SimpleBooleanProperty
import org.eclipse.jgit.lib.BranchConfig
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.SubmoduleConfig
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
    override val name: String get() = branchList.localBranchDisplayName(ref.name)

    /**
     * ローカルブランチのパス
     */
    override val path: String get() = ref.name
    override val localPath: String get() = path
    override val remotePath: String? get() = remoteBranch.get()?.path

    /**
     * 関連付けられているリモートブランチの参照
     */
    var remoteBranch = WeakReference<GvRemoteBranch>(null)

    /**
     * 選択中のブランチである場合、trueを保持する
     */
    val isCurrentBranch get() = (branchList.repository.currentBranch == name)

    /**
     * 表示対象ローカルブランチの場合、trueを保持する
     */
    val selectedFlagProperty = SimpleBooleanProperty(true)

    val hasRemoteConf get() = branchList.repository.remoteConfigList.isNotEmpty()

    fun checkout(monitor: GvProgressMonitor) {
        repository.gitCommand.checkout()
            .setProgressMonitor(monitor)
            .setName(name)
            .call()
        repository.branchChanged()
    }

    fun pull() {
        repository.gitCommand.pull()
            .setRebase(BranchConfig.BranchRebaseMode.REBASE)
            .setRecurseSubmodules(SubmoduleConfig.FetchRecurseSubmodulesMode.ON_DEMAND)
            .call()
        repository.branchChanged()
    }

    fun push(remote: String = Constants.DEFAULT_REMOTE_NAME, tag: Boolean = true) {
        val command = repository.gitCommand.push()
            .add(name)
            .setRemote(remote)
        if (tag) {
            command.setPushTags()
        }
        command.call()
        repository.branchChanged()
    }

    fun mergeToHead(message: String) {
        repository.gitCommand
            .merge()
            .include(ref)
            .setMessage(message)
            .call()
        repository.branchChanged()
    }

    fun remove(force: Boolean) {
        repository.gitCommand.branchDelete()
            .setBranchNames(name)
            .setForce(force)
            .call()
        repository.branchChanged()
    }

    fun rename(newName: String) {
        repository.gitCommand.branchRename()
            .setOldName(name)
            .setNewName(newName)
            .call()
        repository.branchChanged()
    }

    fun rebase() {
        repository.gitCommand.rebase()
            .setUpstream(path)
            .call()
        repository.branchChanged()
    }
}
