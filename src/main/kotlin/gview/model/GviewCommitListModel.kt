package gview.model

import gview.model.branch.GviewLocalBranchModel
import gview.model.branch.GviewRemoteBranchModel
import gview.model.commit.GviewCommitDataModel
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
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
    val commitListProperty= SimpleObjectProperty<List<GviewCommitDataModel>>()

    //Commit情報のリスト(内部形式)
    val plotCommitList = PlotCommitList<PlotLane>()

    //Commit情報 - ID マップ
    val commitMap = mutableMapOf<ObjectId, GviewCommitDataModel>()

    //Commit一覧表示サイズ(暫定)
    private val commitSize = 1000

    //初期化
    init {
        localBranchesProperty.addListener { _, _, newList -> updateBranches(newList) }
    }

    //ローカルブランチ一覧更新時の処理
    private fun updateBranches(localBranches: List<GviewLocalBranchModel>) {
        //各ブランチの選択チェックボックスが変更されたら、コミットリストを更新する
        localBranches.forEach { it.selectedProperty.addListener { _ -> update() } }
    }

    //ローカルブランチのチェックボックス変更時の処理
    private fun update( ) {

        val repository = repositoryProperty.value

        //PlotCommitListインスタンスを生成
        val plotWalk = PlotWalk(repository)
        localBranchesProperty.value.forEach {
            if(it.selected) { plotWalk.markStart(plotWalk.parseCommit(it.ref.objectId)) }
        }
        plotCommitList.clear()
        plotCommitList.source(plotWalk)
        plotCommitList.fillTo(this.commitSize)

        //HEAD IDを取得
        val headId = repository.resolve(Constants.HEAD)

        //Commitモデルに変換
        val commitList = mutableListOf<GviewCommitDataModel>()
        var prev: GviewCommitDataModel? = null
        plotCommitList.forEach {
            val commit = GviewCommitDataModel(repository, this, it, it.id == headId, prev)
            commitList.add(commit)
            prev = commit
        }

        //IDをキーとするMapに変換
        commitMap.clear()
        commitList.forEach { commitMap[it.id] = it }

        //コミット情報からローカルブランチへのリンクを設定
        localBranchesProperty.value.forEach {
            commitMap[it.ref.objectId]?.localBranches?.add(it)
        }

        //コミット情報からリモートブランチへのリンクを設定
        remoteBranchesProperty.value.forEach {
            commitMap[it.ref.objectId]?.remoteBranches?.add(it)
        }

        //コミット情報にタグを設定
        val revWalk = RevWalk(repository)
        Git(repository).tagList().call().forEach {
            val tagName = Repository.shortenRefName(it.name)
            when(val obj = revWalk.parseAny(it.objectId)) {
                is RevTag -> commitMap[obj.getObject().id]?.tags?.add(tagName)
                is RevCommit -> commitMap[obj.id]?.tags?.add(tagName)
            }
        }

        //プロパティを更新
        commitListProperty.value = commitList
    }

}