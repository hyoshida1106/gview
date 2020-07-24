package gview.model

import gview.model.branch.GviewLocalBranchModel
import gview.model.commit.CommitDataModel
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revplot.PlotCommitList
import org.eclipse.jgit.revplot.PlotLane
import org.eclipse.jgit.revplot.PlotWalk

class GviewCommitListModel(private val repositoryProperty: ObjectProperty<Repository>,
                           private val localBranchesProperty: ObjectProperty<List<GviewLocalBranchModel>>) {

    //Commit情報のリスト
    val commitListProperty: ObjectProperty<List<CommitDataModel>>
    private val commitList: List<CommitDataModel> get() { return commitListProperty.value }

    //Commit情報のリスト(内部形式)
    private val plotCommitList = PlotCommitList<PlotLane>()

    //Commit情報 - ID マップ
    private val commitMapProperty: ObjectProperty<Map<ObjectId, CommitDataModel>>
    private val commitMap: Map<ObjectId, CommitDataModel> get() { return commitMapProperty.value }

    //Commit一覧表示サイズ(暫定)
    private val commitSize = 1000

    //初期化
    init {
        commitListProperty = SimpleObjectProperty<List<CommitDataModel>>()
        commitMapProperty = SimpleObjectProperty<Map<ObjectId, CommitDataModel>>()

        localBranchesProperty.addListener { _, _, newList -> updateBranches(newList) }
        commitListProperty.addListener { _, _, newList -> updateCommits(newList) }
    }

    //Commit情報リストの設定
    private fun updateBranches(localBranches: List<GviewLocalBranchModel>) {
        localBranches.forEach { it.selectedProperty.addListener { _ -> updateBranchSel() } }
    }

    //Commit情報の表示選択の設定
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
            val commit = CommitDataModel(repositoryProperty.value, it, prev)
            commitList.add(commit)
            prev = commit
        }
        commitListProperty.value = commitList
    }

    //Commit情報マップの設定
    private fun updateCommits(newCommits: List<CommitDataModel>) {
        val newMap = mutableMapOf<ObjectId, CommitDataModel>()
        newCommits.forEach { newMap[it.id] = it }
        commitMapProperty.value = newMap
    }
}