package gview.model

import gview.model.branch.GviewLocalBranchModel
import gview.model.branch.GviewRemoteBranchModel
import gview.model.commit.GviewCommitDataModel
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

class GviewCommitListModel() {

    //Commit情報のリスト
    val commitListProperty = SimpleObjectProperty<List<GviewCommitDataModel>>()

    //Commit情報のリスト(内部形式)
    val plotCommitList = PlotCommitList<PlotLane>()

    //Commit情報 - ID マップ
    val commitMap = mutableMapOf<ObjectId, GviewCommitDataModel>()

    //Commit一覧表示サイズ(暫定)
    private val commitSize = 1000

    private var repository: Repository? = null
    private var remoteBranches = listOf<GviewRemoteBranchModel>()
    private var localBranches = listOf<GviewLocalBranchModel>()

    //ローカルブランチのチェックボックス変更時の処理
    fun update(newRepository: Repository?, newRemoteBranches: List<GviewRemoteBranchModel>,
               newLocalBranches: List<GviewLocalBranchModel> ) {
        repository = newRepository
        remoteBranches = newRemoteBranches
        localBranches = newLocalBranches
        refresh()
    }

    private fun refresh() {

        if (repository != null) {

            //PlotCommitListインスタンスを生成
            val plotWalk = PlotWalk(repository)
            localBranches.forEach {
                if (it.selected) {
                    plotWalk.markStart(plotWalk.parseCommit(it.ref.objectId))
                }
                it.selectedProperty.addListener { _ -> refresh() }
            }
            plotCommitList.clear()
            plotCommitList.source(plotWalk)
            plotCommitList.fillTo(this.commitSize)

            //HEAD IDを取得
            val headId = repository!!.resolve(Constants.HEAD)

            //Commitモデルに変換
            val commitList = mutableListOf<GviewCommitDataModel>()
            var prev: GviewCommitDataModel? = null
            plotCommitList.forEach {
                val commit = GviewCommitDataModel(repository!!, this, it, it.id == headId, prev)
                commitList.add(commit)
                prev = commit
            }

            //IDをキーとするMapに変換
            commitMap.clear()
            commitList.forEach { commitMap[it.id] = it }

            //コミット情報からローカルブランチへのリンクを設定
            localBranches.forEach { commitMap[it.ref.objectId]?.localBranches?.add(it) }

            //コミット情報からリモートブランチへのリンクを設定
            remoteBranches.forEach { commitMap[it.ref.objectId]?.remoteBranches?.add(it) }

            //コミット情報にタグを設定
            val revWalk = RevWalk(repository)
            Git(repository).tagList().call().forEach {
                val tagName = Repository.shortenRefName(it.name)
                when (val obj = revWalk.parseAny(it.objectId)) {
                    is RevTag -> commitMap[obj.getObject().id]?.tags?.add(tagName)
                    is RevCommit -> commitMap[obj.id]?.tags?.add(tagName)
                }
            }

            //プロパティを更新
            commitListProperty.value = commitList

        } else {
            //所持値を初期化
            commitListProperty.value = listOf()
            plotCommitList.clear()
            commitMap.clear()
        }
    }
}