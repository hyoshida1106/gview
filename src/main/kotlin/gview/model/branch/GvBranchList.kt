package gview.model.branch

import gview.model.GvRepository
import gview.model.commit.GviewCommitDataModel
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.BranchTrackingStatus
import org.eclipse.jgit.lib.Repository
import java.lang.ref.WeakReference


class GvBranchList(private val repository: GvRepository){

    //ローカルブランチリスト
    val localBranchList = SimpleObjectProperty<List<GvLocalBranch>>()

    //リモートブランチリスト
    val remoteBranchList = SimpleObjectProperty<List<GvRemoteBranch>>()

    //現在チェックアウトされているブランチ名
    val currentBranch = SimpleStringProperty("")

    //初期化
    init {
        update()
    }

    fun remoteBranchDisplayName(name: String): String {
        return repository.jgitRepository.shortenRemoteBranchName(name)
    }
    fun localBranchDisplayName(name: String): String {
        return Repository.shortenRefName(name)
    }

    //更新
    private fun update() {

        //Gitインスタンスを共用する
        val git = Git(repository.jgitRepository)

        //リモートブランチの一覧を取得
        //後でローカルブランチと紐付けるためのマップも同時に作成する
        val remoteBranches = mutableListOf<GvRemoteBranch>()
        val remoteBranchMap = mutableMapOf<String, GvRemoteBranch>()
        git.branchList()
            .setListMode(ListBranchCommand.ListMode.REMOTE)
            .call()
            .forEach {
                val remoteBranch = GvRemoteBranch(this, it)
                remoteBranches.add(remoteBranch)
                remoteBranchMap[remoteBranch.path] = remoteBranch
            }
        remoteBranchList.value = remoteBranches

        //ローカルブランチの一覧を取得
        val localBranches = mutableListOf<GvLocalBranch>()
        git.branchList()
            .call()
            .forEach {
                //ローカルブランチ一覧に追加
                val localBranch = GvLocalBranch(this, it)
                localBranches.add(localBranch)
                //リモートブランチが存在する場合、双方向参照を設定する
                val trackingStatus = BranchTrackingStatus.of(repository.jgitRepository, localBranch.path)
                if (trackingStatus != null) {
                    val remoteBranch = remoteBranchMap[trackingStatus.remoteTrackingBranch]
                    if (remoteBranch != null) {
                        localBranch.remoteBranch = WeakReference(remoteBranch)
                        remoteBranch.localBranch = WeakReference(localBranch)
                    }
                }
            }
        localBranchList.value = localBranches

        //現在チェックアウトされているブランチ名
        currentBranch.value = repository.jgitRepository.branch
    }

    //リモートブランチをローカルへチェックアウトする
    fun checkoutRemoteBranch(model: GvRemoteBranch) {
        Git(repository.jgitRepository)
                .checkout()
                .setName(model.name)
                .setStartPoint(model.path)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .setCreateBranch(true)
                .call()
        Platform.runLater { update() }
    }

    //ローカルブランチをチェックアウトしてカレントブランチにする
    fun checkoutLocalBranch(model: GvLocalBranch) {
        Git(repository.jgitRepository)
                .checkout()
                .setName(model.name)
                .call()
        Platform.runLater { update() }
    }

    //ローカルブランチを削除する
    fun removeLocalBranch(model: GvLocalBranch, force: Boolean) {
        Git(repository.jgitRepository)
                .branchDelete()
                .setBranchNames(model.name)
                .setForce(force)
                .call()
        Platform.runLater { update() }
    }

    //HEADからブランチを作成する
    fun createNewBranchFromHead(newBranch: String, checkout: Boolean) {
        if(checkout) {
            Git(repository.jgitRepository)
                    .checkout()
                    .setName(newBranch)
                    .setCreateBranch(true)
                    .call()
        } else {
            Git(repository.jgitRepository)
                    .branchCreate()
                    .setName(newBranch)
                    .call()
        }
        Platform.runLater { update() }
    }

    //指定したコミットからブランチを作成する
    fun createNewBranchFromCommit(newBranch: String, commit: GviewCommitDataModel, checkout: Boolean) {
        if(checkout) {
            Git(repository.jgitRepository)
                    .checkout()
                    .setName(newBranch)
                    .setStartPoint(commit.revCommit)
                    .setCreateBranch(true)
                    .call()
        } else {
            Git(repository.jgitRepository)
                    .branchCreate()
                    .setName(newBranch)
                    .setStartPoint(commit.revCommit)
                    .call()
        }
        Platform.runLater { update() }
    }

    //指定したブランチから新しいブランチを作成する
    fun createNewBranchFromOtherBranch(newBranch: String, model: GvLocalBranch, checkout: Boolean) {
        if(checkout) {
            Git(repository.jgitRepository)
                    .checkout()
                    .setName(newBranch)
                    .setStartPoint(model.path)
                    .setCreateBranch(true)
                    .call()
        } else {
            Git(repository.jgitRepository)
                    .branchCreate()
                    .setName(newBranch)
                    .setStartPoint(model.path)
                    .call()
        }
        Platform.runLater { update() }
    }

    //指定したコミットをHEADへマージする
	fun mergeCommit(model: GviewCommitDataModel, message: String) {
        Git(repository.jgitRepository)
                .merge()
                .include(model.id)
                .setMessage(message)
                .call()
        Platform.runLater { update() }
	}
}