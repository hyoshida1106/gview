package gview.model

import javafx.beans.property.SimpleObjectProperty
import org.eclipse.jgit.events.RepositoryEvent
import org.eclipse.jgit.events.RepositoryListener
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revplot.PlotCommitList
import org.eclipse.jgit.revplot.PlotLane
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTag

/**
 * コミット情報リストモデル
 */
class GvCommitList(private val repository: GvRepository) {

    class CommitChangedEvent : RepositoryEvent<CommitChangedListener>() {
        override fun getListenerType(): Class<CommitChangedListener> {
            return CommitChangedListener::class.java
        }
        override fun dispatch(listener: CommitChangedListener?) {
            listener?.onCommitChanged(this)
        }
    }
    fun interface CommitChangedListener : RepositoryListener {
        fun onCommitChanged(event: CommitChangedEvent)
    }

    //Commit情報のリスト
    val commitList = SimpleObjectProperty<List<GvCommit>>()

    //Commit情報のリスト(内部形式)
    val plotCommitList = PlotCommitList<PlotLane>()

    //Commit情報 - ID マップ
    val commitIdMap = mutableMapOf<ObjectId, GvCommit>()

    //Commit情報 - タグ名マップ
    val commitTagMap = mutableMapOf<String, GvCommit>()

    //HEADのObject ID
    var headId: ObjectId? = null

    //HEADのレーン番号
    var headerLaneNumber: Int? = null

    //Commit一覧表示サイズ(暫定)
    private val commitSize = 1000

    /**
     * 初期化
     */
    init {
        updateModel()
        repository.addCommitChangedListener { updateModel() }
    }

    /**
     * 更新処理
     */
    private fun updateModel() {
        //所持値を初期化
        headId = repository.head
        //PlotCommitListインスタンスを生成
        plotCommitList.clear()
        repository.getPlotWalk().use { plotWalk ->
            repository.branches.localBranchList.value
                .filter { it.selectedFlagProperty.value }
                .forEach { plotWalk.markStart(plotWalk.parseCommit(it.ref.objectId)) }
            plotCommitList.source(plotWalk)
            plotCommitList.fillTo(this.commitSize)
        }
        //Commitモデルに変換
        val commits = mutableListOf<GvCommit>()
        var prev: GvCommit? = null
        plotCommitList.forEach {
            val commit = GvCommit(it, repository, this, prev)
            commits.add(commit)
            prev = commit
        }
        //IDをキーとするMapに変換
        commitIdMap.putAll(commits.associateBy { it.id })
        //WorkFileからHEADまでの線を描く
        val head = commitIdMap[headId]
        if (head != null && commits.indexOf(head) >= 0) {
            //HEAD直前までのパスを重複しないように描く
            var lane = head.laneNumber
            val headerPath = commits.subList(0, commits.indexOf(head))
            val maxLineNumber = headerPath.maxOfOrNull { it.maxLaneNumber } ?: -1
            if (lane <= maxLineNumber) {
                lane = maxLineNumber + 1
            }
            headerPath.forEach { it.headerLane = lane }
            head.headerLane = lane
            //WorkFileのレーン番号を更新
            headerLaneNumber = lane
        }
        //コミット情報からローカルブランチへのリンクを設定
        repository.branches.localBranchList.value
            .forEach { commitIdMap[it.ref.objectId]?.localBranches?.add(it) }
        //コミット情報からリモートブランチへのリンクを設定
        repository.branches.remoteBranchList.value
            .forEach { commitIdMap[it.ref.objectId]?.remoteBranches?.add(it) }
        //コミット情報にタグを設定
        commitTagMap.clear()
        repository.getRevWalk().use { revWalk ->
            repository.gitCommand
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
        }
        //コミットリストを保存
        commitList.value = commits
    }
}
