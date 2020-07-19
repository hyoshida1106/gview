package gview.model

import gview.GViewApp
import gview.model.branch.GviewLocalBranchModel
import gview.model.branch.GviewRemoteBranchModel
import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.BranchTrackingStatus
import org.eclipse.jgit.lib.Repository

class GviewBranchListModel {

    //ローカルブランチリスト
    val localBranchesProperty: ObjectProperty<List<GviewLocalBranchModel>>

    //リモートブランチリスト
    val remoteBranchesProperty: ObjectProperty<List<GviewRemoteBranchModel>>

    //初期化
    init {
        localBranchesProperty  = SimpleObjectProperty<List<GviewLocalBranchModel >>()
        remoteBranchesProperty = SimpleObjectProperty<List<GviewRemoteBranchModel>>()
    }

    //更新
    fun update(repository: Repository) {

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
        val localBranches = mutableListOf<GviewLocalBranchModel >()
        git.branchList().call().forEach {
            val localBranch = GviewLocalBranchModel(it)
            localBranches.add(localBranch)
            //リモートブランチが存在する場合、双方向参照を設定する
            val remotePath = BranchTrackingStatus.of(repository, localBranch.path).remoteTrackingBranch
            val remoteBranch = remoteBranchMap[remotePath]
            if(remoteBranch != null) {
                localBranch.remoteBranch = remoteBranch
                remoteBranch.localBranch = localBranch
            }
        }
        localBranchesProperty.value = localBranches
    }
}