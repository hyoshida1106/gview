package gview.model.branch

import gview.model.GviewRepositoryModel
import gview.model.commit.GviewCommitDataModel
import gview.model.util.ModelObservable
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.BranchTrackingStatus
import org.eclipse.jgit.lib.Repository


class GviewBranchListModel(private val repository: GviewRepositoryModel)
    : ModelObservable<GviewBranchListModel>() {

    //ローカルブランチリスト
    var localBranches = mutableListOf<GviewLocalBranchModel>()

    //リモートブランチリスト
    var remoteBranches = mutableListOf<GviewRemoteBranchModel>()

    //現在チェックアウトされているブランチ名
    var currentBranch: String = ""

    //更新
    fun update() {
        localBranches.clear()
        remoteBranches.clear()

        if(repository.isValid) {

            //JGitのRepositoryインスタンス
            val jgitRepository = repository.getJgitRepository()

            //Gitインスタンスを共用する
            val git = Git(jgitRepository)

            //リモートブランチの一覧を取得
            //後でローカルブランチと紐付けるためのマップも同時に作成する
            val remoteBranchMap = mutableMapOf<String, GviewRemoteBranchModel>()
            git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE)
                    .call()
                    .filterNot { repository.getJgitRepository().shortenRemoteBranchName(it.name) == null }
                    .filterNot { repository.getJgitRepository().shortenRemoteBranchName(it.name) == "HEAD" }
                    .forEach {
                        val remoteBranch = GviewRemoteBranchModel(this, it, jgitRepository)
                        remoteBranches.add(remoteBranch)
                        remoteBranchMap[remoteBranch.path] = remoteBranch
                    }

            //現在チェックアウトされているブランチ名
            currentBranch = jgitRepository.branch

            //ローカルブランチの一覧を取得
            git.branchList()
                    .call()
                    .filterNot { Repository.shortenRefName(it.name) == "HEAD" }
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
        } else {
            currentBranch = ""
        }

        fireCallback(this)
    }

    //リモートブランチをローカルへチェックアウトする
    fun checkoutRemoteBranch(model: GviewRemoteBranchModel) {
        Git(repository.getJgitRepository())
                .checkout()
                .setName(model.name)
                .setStartPoint(model.path)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .setCreateBranch(true)
                .call()
        repository.workFileInfo.update()
        update()
    }

    //ローカルブランチをチェックアウトしてカレントブランチにする
    fun checkoutLocalBranch(model: GviewLocalBranchModel) {
        Git(repository.getJgitRepository())
                .checkout()
                .setName(model.name)
                .call()
        repository.workFileInfo.update()
        update()
    }

    //指定したコミットをチェックアウトする
    fun checkoutCommit(model: GviewCommitDataModel) {
        Git(repository.getJgitRepository())
                .checkout()
                .setStartPoint(model.revCommit)
                .call()
        repository.workFileInfo.update()
        update()
    }

    //ローカルブランチを削除する
    fun removeLocalBranch(model: GviewLocalBranchModel, force: Boolean) {
        Git(repository.getJgitRepository())
                .branchDelete()
                .setBranchNames(model.name)
                .setForce(force)
                .call()
        repository.workFileInfo.update()
        update()
    }

    //HEADからブランチを作成する
    fun createNewBranchFromHead(newBranch: String, checkout: Boolean) {
        if(checkout) {
            Git(repository.getJgitRepository())
                    .checkout()
                    .setName(newBranch)
                    .setCreateBranch(true)
                    .call()
        } else {
            Git(repository.getJgitRepository())
                    .branchCreate()
                    .setName(newBranch)
                    .call()
        }
        update()
    }

    //指定したコミットからブランチを作成する
    fun createNewBranchFromCommit(newBranch: String, commit: GviewCommitDataModel, checkout: Boolean) {
        if(checkout) {
            Git(repository.getJgitRepository())
                    .checkout()
                    .setName(newBranch)
                    .setStartPoint(commit.revCommit)
                    .setCreateBranch(true)
                    .call()
        } else {
            Git(repository.getJgitRepository())
                    .branchCreate()
                    .setName(newBranch)
                    .setStartPoint(commit.revCommit)
                    .call()
        }
        update()
    }

    //指定したブランチから新しいブランチを作成する
    fun createNewBranchFromOtherBranch(newBranch: String, model: GviewLocalBranchModel, checkout: Boolean) {
        if(checkout) {
            Git(repository.getJgitRepository())
                    .checkout()
                    .setName(newBranch)
                    .setStartPoint(model.path)
                    .setCreateBranch(true)
                    .call()
        } else {
            Git(repository.getJgitRepository())
                    .branchCreate()
                    .setName(newBranch)
                    .setStartPoint(model.path)
                    .call()
        }
        update()
    }
}