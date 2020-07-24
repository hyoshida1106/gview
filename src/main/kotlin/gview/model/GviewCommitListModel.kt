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
    private val commitListProperty: ObjectProperty<PlotCommitList<PlotLane>>
    private val commitList: PlotCommitList<PlotLane> get() { return commitListProperty.value }

    //Commit情報 - ID マップ
    private val commitMapProperty: ObjectProperty<Map<ObjectId, CommitDataModel>>
    private val commitMap: Map<ObjectId, CommitDataModel> get() { return commitMapProperty.value }

    private val commitSize = 1000

    //初期化
    init {
        commitListProperty = SimpleObjectProperty<PlotCommitList<PlotLane>>()
        commitMapProperty = SimpleObjectProperty<Map<ObjectId, CommitDataModel>>()

        localBranchesProperty.addListener { _, _, newList -> update(newList) }
        commitListProperty.addListener { _, _, newList -> update(newList) }
    }

    //Commit情報リストの設定
    private fun update(localBranches: List<GviewLocalBranchModel>) {
        localBranches.forEach { it.selectedProperty.addListener { _ -> update(it) } }
    }

    //Commit情報の表示選択の設定
    private fun update(branch: GviewLocalBranchModel) {
        val walk = PlotWalk(repositoryProperty.value)
        localBranchesProperty.value.forEach {
            if(it.selected) { walk.markStart(walk.parseCommit(it.ref.objectId)) }
        }
        val list = PlotCommitList<PlotLane>()
        list.source(walk)
        list.fillTo(this.commitSize)
        commitListProperty.value = list
    }

    //Commit情報マップの設定
    private fun update(newCommits: PlotCommitList<PlotLane>) {
        val newMap = mutableMapOf<ObjectId, CommitDataModel>()
        var prev: CommitDataModel? = null
        newCommits.forEach {
            val commit = CommitDataModel(repositoryProperty.value, it, prev)
            newMap[it.id] = commit
            prev = commit
        }
        commitMapProperty.value = newMap
    }
}