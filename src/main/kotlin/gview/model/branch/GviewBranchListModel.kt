package gview.model.branch

import gview.model.commit.GviewCommitListModel
import gview.model.GviewRepositoryModel
import gview.model.util.ModelObservable
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.BranchTrackingStatus


class GviewBranchListModel(private val repository: GviewRepositoryModel)
    : ModelObservable<GviewBranchListModel>() {

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

        val jgitRepository = repository.jgitRepository
        if(jgitRepository != null) {

            //Gitインスタンスを共用する
            val git = Git(jgitRepository)

            //リモートブランチの一覧を取得
            //後でローカルブランチと紐付けるためのマップも同時に作成する
            val remoteBranchMap = mutableMapOf<String, GviewRemoteBranchModel>()
            git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE)
                    .call()
                    .filterNot { jgitRepository.shortenRemoteBranchName(it.name) == "HEAD" }
                    .forEach {
                        val remoteBranch = GviewRemoteBranchModel(this, it, jgitRepository)
                        remoteBranches.add(remoteBranch)
                        remoteBranchMap[remoteBranch.path] = remoteBranch
                    }

            //現在チェックアウトされているブランチ名
            currentBranch = repository.jgitRepository!!.branch

            //ローカルブランチの一覧を取得
            git.branchList()
                    .call()
                    .forEach {
                        //ローカルブランチ一覧に追加
                        val localBranch = GviewLocalBranchModel(this, it)
                        localBranches.add(localBranch)
                        //リモートブランチが存在する場合、双方向参照を設定する
                        val trackingStatus = BranchTrackingStatus.of(jgitRepository, localBranch.path)
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
        repository.headerFiles.update()
        update()
    }

    //ローカルブランチをチェックアウトしてカレントブランチにする
    fun checkoutLocalBranch(model: GviewLocalBranchModel) {
        Git(repository.jgitRepository).checkout()
                .setName(model.name)
                .call()
        repository.headerFiles.update()
        update()
    }

    fun removeLocalBranch(model: GviewLocalBranchModel, force: Boolean) {
        Git(repository.jgitRepository).branchDelete()
                .setBranchNames(model.name)
                .setForce(force)
                .call()
        repository.headerFiles.update()
        update()
    }
}