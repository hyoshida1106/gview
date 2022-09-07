package gview.model.branch

import gview.model.GvProgressMonitor
import gview.model.GvRepository
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.transport.RefSpec
import java.lang.ref.WeakReference

/**
 * リモートブランチモデル
 *
 *  @constructor            プライマリコンストラクタ
 *  @param[branchList]      保持されるブランチリスト
 *  @param[ref]             ブランチ情報
 */
class GvRemoteBranch(branchList: GvBranchList, ref: Ref) : GvBranch(branchList, ref) {

    /**
     * リモートブランチ名称
     *
     * パスから取得する。
     */
    override val name: String get() = branchList.remoteBranchDisplayName(ref.name)

    /**
     * リモートブランチのパス
     */
    override val path: String get() = ref.name
    override val localPath: String? get() = localBranch.get()?.path
    override val remotePath: String get() = path

    /**
     * 関連付けられているローカルブランチの参照
     */
    var localBranch = WeakReference<GvLocalBranch>(null)

    fun checkout(monitor: GvProgressMonitor) {
        repository.gitCommand.checkout()
            .setProgressMonitor(monitor)
            .setName(name)
            .setStartPoint(path)
            .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
            .setCreateBranch(true)
            .call()
        repository.branchChanged()
    }

    /**
     * ブランチの削除
     */
    fun remove() {
        if(repository.remoteConfigList.isEmpty()) return
        val remoteName = repository.remoteConfigList[0].name
        val destination = path.replace(Constants.R_REMOTES + remoteName + "/", Constants.R_HEADS)
        val refSpec = RefSpec().setSource(null).setDestination(destination)
        val git = repository.gitCommand
        git.branchDelete().setBranchNames(name).setForce(true).call()
        git.push().setRefSpecs(refSpec).setRemote(remoteName).call()
        repository.branchChanged()
    }
}