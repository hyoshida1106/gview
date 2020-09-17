package gview.model

import gview.model.branch.GviewLocalBranchModel
import gview.model.branch.GviewRemoteBranchModel
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.BranchTrackingStatus
import org.eclipse.jgit.lib.Repository
import java.lang.Exception


class GviewBranchListModel() {

    //ローカルブランチリスト
    val localBranchesProperty = SimpleObjectProperty<List<GviewLocalBranchModel>>()

    //リモートブランチリスト
    val remoteBranchesProperty = SimpleObjectProperty<List<GviewRemoteBranchModel>>()

    //コミット情報リスト
    val commits = GviewCommitListModel()

    private var repository: Repository? = null

    //更新
    fun update(newRepository: Repository?) {
        repository = newRepository
        refresh()
    }

    private fun refresh() {
        var localBranches = mutableListOf<GviewLocalBranchModel>()
        var remoteBranches = mutableListOf<GviewRemoteBranchModel>()

        if(repository != null) {

            //Gitインスタンスを共用する
            val git = Git(repository)

            //リモートブランチの一覧を取得
            //後でローカルブランチと紐付けるためのマップも同時に作成する
            val remoteBranchMap = mutableMapOf<String, GviewRemoteBranchModel>()
            git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call().forEach {
                val remoteBranch = GviewRemoteBranchModel(this, it, repository!!)
                remoteBranches.add(remoteBranch)
                remoteBranchMap[remoteBranch.path] = remoteBranch
            }

            //ローカルブランチの一覧を取得
            git.branchList().call().forEach {
                //ローカルブランチ一覧に追加
                val localBranch = GviewLocalBranchModel(this, it)
                localBranches.add(localBranch)
                //リモートブランチが存在する場合、双方向参照を設定する
                val trackingStatus = BranchTrackingStatus.of(repository, localBranch.path)
                if (trackingStatus != null) {
                    val remoteBranch = remoteBranchMap[trackingStatus.remoteTrackingBranch]
                    if (remoteBranch != null) {
                        localBranch.remoteBranch = remoteBranch
                        remoteBranch.localBranch = localBranch
                    }
                }
            }
        }

        commits.update(repository, remoteBranches, localBranches)

        Platform.runLater {
            remoteBranchesProperty.value = remoteBranches
            localBranchesProperty.value = localBranches
        }
    }

    fun checkoutRemoteBranch(model: GviewRemoteBranchModel) {
        Git(repository).checkout()
                .setName(model.name)
                .setStartPoint(model.path)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .setCreateBranch(true)
                .call()
        refresh()
    }
}