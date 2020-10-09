package gview.model.commit

import gview.model.GviewRepositoryModel
import gview.model.util.ModelObservable
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revplot.PlotCommitList
import org.eclipse.jgit.revplot.PlotLane
import org.eclipse.jgit.revplot.PlotWalk
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTag
import org.eclipse.jgit.revwalk.RevWalk

class GviewCommitListModel(
        private val repository: GviewRepositoryModel)
    : ModelObservable<GviewCommitListModel>() {

    //Commit情報のリスト
    val commitList = mutableListOf<GviewCommitDataModel>()

    //Commit情報のリスト(内部形式)
    val plotCommitList = PlotCommitList<PlotLane>()

    //Commit情報 - ID マップ
    val commitMap = mutableMapOf<ObjectId, GviewCommitDataModel>()

    //Commit一覧表示サイズ(暫定)
    private val commitSize = 1000

    //リポジトリ変更時の処理
    fun update() {
        //ローカルブランチの選択が変更された場合のリスナを設定
        repository.branches.localBranches.forEach {
            it.clearListeners()
            it.addListener { refresh() }
        }
        refresh()
    }

    //表示更新
    fun refresh() {
        commitList.clear()
        val jgitRepository = repository.jgitRepository
        if (jgitRepository != null) {

            //PlotCommitListインスタンスを生成
            val plotWalk = PlotWalk(jgitRepository)
            repository.branches.localBranches
                    .filter { it.selected }
                    .forEach { plotWalk.markStart(plotWalk.parseCommit(it.ref.objectId)) }
            plotCommitList.clear()
            plotCommitList.source(plotWalk)
            plotCommitList.fillTo(this.commitSize)
            plotWalk.close()

            //HEAD IDを取得
            val headId = repository.workFileInfo.headId

            //Commitモデルに変換
            var prev: GviewCommitDataModel? = null
            plotCommitList
                    .forEach {
                        val commit = GviewCommitDataModel(
                                jgitRepository,
                                this,
                                it,
                                it.id == headId,
                                prev)
                        commitList.add(commit)
                        prev = commit
                    }

            //IDをキーとするMapに変換
            commitMap.clear()
            commitList.forEach { commitMap[it.id] = it }

            //コミット情報からローカルブランチへのリンクを設定
            repository.branches.localBranches
                    .forEach { commitMap[it.ref.objectId]?.localBranches?.add(it) }

            //コミット情報からリモートブランチへのリンクを設定
            repository.branches.remoteBranches
                    .forEach { commitMap[it.ref.objectId]?.remoteBranches?.add(it) }

            //コミット情報にタグを設定
            val revWalk = RevWalk(jgitRepository)
            Git(jgitRepository)
                    .tagList()
                    .call()
                    .forEach {
                        val tagName = Repository.shortenRefName(it.name)
                        when (val obj = revWalk.parseAny(it.objectId)) {
                            is RevTag -> commitMap[obj.getObject().id]?.tags?.add(tagName)
                            is RevCommit -> commitMap[obj.id]?.tags?.add(tagName)
                        }
            }
            revWalk.close()

        } else {
            //所持値を初期化
            plotCommitList.clear()
            commitMap.clear()
        }

        fireCallback(this)
    }
}