package gview.model

import gview.model.branch.GviewLocalBranchModel
import gview.model.branch.GviewRemoteBranchModel
import gview.model.util.ModelObservable
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.BranchTrackingStatus


class GviewBranchListModel(private val repository: GviewRepositoryModel): ModelObservable<GviewBranchListModel>() {

    //ローカルブランチリスト
    var localBranches = mutableListOf<GviewLocalBranchModel>()

    //リモートブランチリスト
    var remoteBranches = mutableListOf<GviewRemoteBranchModel>()

    //コミット情報リスト
    val commits = GviewCommitListModel(repository)

    //現在チェックアウトされているブランチ名
    var currentBranch: String? = null

    //更新
    fun update() {
        localBranches.clear()
        remoteBranches.clear()

        if(repository.jgitRepository != null) {

            //Gitインスタンスを共用する
            val git = Git(repository.jgitRepository)

            //リモートブランチの一覧を取得
            //後でローカルブランチと紐付けるためのマップも同時に作成する
            val remoteBranchMap = mutableMapOf<String, GviewRemoteBranchModel>()
            git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call().forEach {
                val remoteBranch = GviewRemoteBranchModel(this, it, repository.jgitRepository!!)
                remoteBranches.add(remoteBranch)
                remoteBranchMap[remoteBranch.path] = remoteBranch
            }

            //現在チェックアウトされているブランチ名
            currentBranch = repository.jgitRepository!!.branch

            //ローカルブランチの一覧を取得
            git.branchList().call().forEach {
                //ローカルブランチ一覧に追加
                val localBranch = GviewLocalBranchModel(this, it)
                localBranches.add(localBranch)
                //リモートブランチが存在する場合、双方向参照を設定する
                val trackingStatus = BranchTrackingStatus.of(repository.jgitRepository, localBranch.path)
                if (trackingStatus != null) {
                    val remoteBranch = remoteBranchMap[trackingStatus.remoteTrackingBranch]
                    if (remoteBranch != null) {
                        localBranch.remoteBranch = remoteBranch
                        remoteBranch.localBranch = localBranch
                    }
                }
            }
        }

        commits.update()

        fireCallback(this)
    }

    //リモートブランチをローカルへチェックアウトする
    fun checkoutRemoteBranch(model: GviewRemoteBranchModel) {
        Git(repository.jgitRepository).checkout()
                .setName(model.name)
                .setStartPoint(model.path)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .setCreateBranch(true)
                .call()
        update()
    }

    fun checkoutLocalBranch(model: GviewLocalBranchModel) {
        Git(repository.jgitRepository).checkout()
                .setName(model.name)
                .call()
        repository.headerFiles.update()
        update()
    }
}