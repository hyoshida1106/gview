package gview.model.commit

import gview.model.GviewRepositoryModel
import gview.model.util.ModelObservable
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

class GviewCommitListModel(private val repository: GviewRepositoryModel)
    : ModelObservable<GviewCommitListModel>() {

    //Commit情報のリスト
    val commitList = mutableListOf<GviewCommitDataModel>()

    //Commit情報のリスト(内部形式)
    val plotCommitList = PlotCommitList<PlotLane>()

    //Commit情報 - ID マップ
    val commitIdMap = mutableMapOf<ObjectId, GviewCommitDataModel>()

    //Commit情報 - タグ名マップ
    val commitTagMap = mutableMapOf<String, GviewCommitDataModel>()

    //HEADのObject ID
    var headId: ObjectId? = null

    //HEADのレーン番号
    var headerLaneNumber: Int? = null

    //Commit一覧表示サイズ(暫定)
    private val commitSize = 1000

    init {
        repository.branches.addListener { update() }
    }

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
        //所持値を初期化
        commitList.clear()
        plotCommitList.clear()
        commitIdMap.clear()

        if(repository.isValid) {

            val jgitRepository = repository.getJgitRepository()

            headId = jgitRepository.resolve(Constants.HEAD)

            //PlotCommitListインスタンスを生成
            val plotWalk = PlotWalk(jgitRepository)
            repository.branches.localBranches
                    .filter { it.selected }
                    .forEach { plotWalk.markStart(plotWalk.parseCommit(it.ref.objectId)) }
            plotCommitList.source(plotWalk)
            plotCommitList.fillTo(this.commitSize)
            plotWalk.close()

            //Commitモデルに変換
            var prev: GviewCommitDataModel? = null
            plotCommitList.forEach {
                val commit = GviewCommitDataModel(jgitRepository, this, it, prev)
                commitList.add(commit)
                prev = commit
            }

            //IDをキーとするMapに変換
            commitIdMap.putAll(commitList.map { it.id to it }.toMap())

            //WorkFileからHEADまでの線を描く
            val head = commitIdMap[headId]
            if(head != null) {
                //HEAD直前までのパスを重複しないように描く
                var lane = head.laneNumber
                val headerPath = commitList.subList(0, commitList.indexOf(head))
                while(headerPath.find { it.laneNumber == lane || it.passWays.contains(lane) } != null) {
                    lane += 1
                }
                headerPath.forEach { it.passWays.add(lane) }
                //HEADからの分岐線を描く
                if(!head.branchTo.contains(lane)) { head.branchTo.add(lane) }
                //WorkFileのレーン番号を更新
                headerLaneNumber = lane
            }

            //コミット情報からローカルブランチへのリンクを設定
            repository.branches.localBranches
                    .forEach { commitIdMap[it.ref.objectId]?.localBranches?.add(it) }

            //コミット情報からリモートブランチへのリンクを設定
            repository.branches.remoteBranches
                    .forEach { commitIdMap[it.ref.objectId]?.remoteBranches?.add(it) }

            //コミット情報にタグを設定
            commitTagMap.clear()
            val revWalk = RevWalk(jgitRepository)
            try {
                Git(jgitRepository)
                        .tagList()
                        .call()
                        .forEach {
                            val tagName = Repository.shortenRefName(it.name)
                            val commit = when (val obj = revWalk.parseAny(it.objectId)) {
                                is RevTag -> commitIdMap[obj.getObject().id]
                                is RevCommit -> commitIdMap[obj.id]
                                else -> null
                            }
                            if (commit != null) {
                                commit.tags.add(tagName)
                                commitTagMap[tagName] = commit
                            }
                        }
            } finally {
                revWalk.close()
            }
        }

        fireCallback(this)
    }
}