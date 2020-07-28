package gview.model

import gview.model.branch.GviewLocalBranchModel
import gview.model.branch.GviewRemoteBranchModel
import gview.model.commit.CommitDataModel
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revplot.PlotCommitList
import org.eclipse.jgit.revplot.PlotLane
import org.eclipse.jgit.revplot.PlotWalk
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTag
import org.eclipse.jgit.revwalk.RevWalk

class GviewCommitListModel(private val repositoryProperty: ObjectProperty<Repository>,
                           private val localBranchesProperty: ObjectProperty<List<GviewLocalBranchModel>>,
                           private val remoteBranchesProperty: ObjectProperty<List<GviewRemoteBranchModel>>) {

    //Commit情報のリスト
    val commitListProperty: ObjectProperty<List<CommitDataModel>>
    val commitList: List<CommitDataModel> get() { return commitListProperty.value }

    //Commit情報のリスト(内部形式)
    val plotCommitList = PlotCommitList<PlotLane>()

    //Commit情報 - ID マップ
    val commitMapProperty: ObjectProperty<Map<ObjectId, CommitDataModel>>
    val commitMap: Map<ObjectId, CommitDataModel> get() { return commitMapProperty.value }

    //Commit一覧表示サイズ(暫定)
    private val commitSize = 1000

    //初期化
    init {
        commitListProperty = SimpleObjectProperty<List<CommitDataModel>>()
        commitMapProperty = SimpleObjectProperty<Map<ObjectId, CommitDataModel>>()

        localBranchesProperty.addListener { _, _, newList -> updateBranches(newList) }
        commitListProperty.addListener { _, _, newList -> updateCommits(newList) }
    }

    //ローカルブランチ一覧更新時の処理
    private fun updateBranches(localBranches: List<GviewLocalBranchModel>) {
        //各ブランチの選択チェックボックスが変更されたら、コミットリストを更新する
        localBranches.forEach { it.selectedProperty.addListener { _ -> updateBranchSel() } }
    }

    //ローカルブランチのチェックボックス変更時の処理
    private fun updateBranchSel( ) {

        //PlotCommitListインスタンスを生成
        val walk = PlotWalk(repositoryProperty.value)
        localBranchesProperty.value.forEach {
            if(it.selected) { walk.markStart(walk.parseCommit(it.ref.objectId)) }
        }
        plotCommitList.clear()
        plotCommitList.source(walk)
        plotCommitList.fillTo(this.commitSize)

        //Commitモデルに変換
        val commitList = mutableListOf<CommitDataModel>()
        var prev: CommitDataModel? = null
        plotCommitList.forEach {
            val commit = CommitDataModel(repositoryProperty.value, this, it, prev)
            commitList.add(commit)
            prev = commit
        }

        //プロパティを更新
        commitListProperty.value = commitList
    }

    //コミット一覧が変更された場合の処理
    private fun updateCommits(newCommits: List<CommitDataModel>) {

        //IDをキーとするMapに変換
        val newMap = mutableMapOf<ObjectId, CommitDataModel>()
        newCommits.forEach { newMap[it.id] = it }

        //コミット情報からローカルブランチへのリンクを設定
        localBranchesProperty.value.forEach {
            newMap[it.ref.objectId]?.localBranches?.add(it)
        }

        //コミット情報からリモートブランチへのリンクを設定
        remoteBranchesProperty.value.forEach {
            newMap[it.ref.objectId]?.remoteBranches?.add(it)
        }

        //コミット情報にタグを設定
        val walk = RevWalk(repositoryProperty.value)
        Git(repositoryProperty.value).tagList().call().forEach {
            val tagName = Repository.shortenRefName(it.name)
            when(val obj = walk.parseAny(it.objectId)) {
                is RevTag -> newMap[obj.getObject().id]?.tags?.add(tagName)
                is RevCommit -> newMap[obj.id]?.tags?.add(tagName)
            }
        }

        //プロパティを更新
        commitMapProperty.value = newMap
    }
}