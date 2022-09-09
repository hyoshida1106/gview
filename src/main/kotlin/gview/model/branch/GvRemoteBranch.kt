package gview.model.branch

import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ProgressMonitor
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

    fun checkout(monitor: ProgressMonitor) {
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
    fun remove(monitor: ProgressMonitor) {
        if(repository.remoteConfigList.isEmpty()) return
        val git = repository.gitCommand
        //ローカルブランチの削除
        git.branchDelete()
            .setBranchNames(name)
            .setForce(true)
            .call()
        //リモートブランチの削除
        val remoteName = repository.remoteConfigList[0].name
        val destination = path.replace(Constants.R_REMOTES + remoteName + "/", Constants.R_HEADS)
        val refSpec = RefSpec().setSource(null).setDestination(destination)
        git.push()
            .setProgressMonitor(monitor)
            .setRefSpecs(refSpec)
            .setRemote(remoteName)
            .call()
        //ブランチ更新
        repository.fetch()
    }
}