package gview.model

import gview.model.branch.GviewLocalBranchModel
import gview.model.branch.GviewRemoteBranchModel
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.BranchTrackingStatus
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository


class GviewBranchListModel(repositoryProperty: ObjectProperty<Repository>) {

    //ローカルブランチリスト
    val localBranchesProperty: ObjectProperty<List<GviewLocalBranchModel>>

    //リモートブランチリスト
    val remoteBranchesProperty: ObjectProperty<List<GviewRemoteBranchModel>>

    //インデックス未登録/登録済ファイル情報
    val headFiles: GviewHeadFilesModel

    //コミット情報リスト
    val commits: GviewCommitListModel

    //HEADのObject ID
    private val headIdProperty: ObjectProperty<ObjectId?>

    //初期化
    init {
        localBranchesProperty  = SimpleObjectProperty<List<GviewLocalBranchModel >>()
        remoteBranchesProperty = SimpleObjectProperty<List<GviewRemoteBranchModel>>()
        headIdProperty = SimpleObjectProperty<ObjectId?>()

        headFiles = GviewHeadFilesModel(repositoryProperty, headIdProperty)
        commits = GviewCommitListModel(repositoryProperty, localBranchesProperty, remoteBranchesProperty)

        //ブランチ一覧の更新
        repositoryProperty.addListener { _, _, newRepository -> update(newRepository) }
    }

    //更新
    private fun update(repository: Repository) {

        //HEAD IDを更新
        headIdProperty.value = repository.resolve(Constants.HEAD)

        //Gitインスタンスを共用する
        val git = Git(repository)

        //リモートブランチの一覧を取得
        //後でローカルブランチと紐付けるためのマップも同時に作成する
        val remoteBranches = mutableListOf<GviewRemoteBranchModel>()
        val remoteBranchMap = mutableMapOf<String, GviewRemoteBranchModel>()
        git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call().forEach {
            val remoteBranch = GviewRemoteBranchModel(it, repository)
            remoteBranches.add(remoteBranch)
            remoteBranchMap[remoteBranch.path] = remoteBranch
        }
        remoteBranchesProperty.value = remoteBranches

        //ローカルブランチの一覧を取得
        val localBranches = mutableListOf<GviewLocalBranchModel>()
        git.branchList().call().forEach {
            //ローカルブランチ一覧に追加
            val localBranch = GviewLocalBranchModel(it)
            localBranches.add(localBranch)
            //リモートブランチが存在する場合、双方向参照を設定する
            val trackingStatus = BranchTrackingStatus.of(repository, localBranch.path)
            if(trackingStatus != null) {
                val remoteBranch = remoteBranchMap[trackingStatus.remoteTrackingBranch]
                if (remoteBranch != null) {
                    localBranch.remoteBranch = remoteBranch
                    remoteBranch.localBranch = localBranch
                }
            }
        }
        localBranchesProperty.value = localBranches
    }
}