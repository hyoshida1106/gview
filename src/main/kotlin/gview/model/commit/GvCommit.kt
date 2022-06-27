package gview.model.commit

import gview.model.branch.GvLocalBranch
import gview.model.branch.GvRemoteBranch
import gview.model.util.ByteArrayDiffFormatter
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revplot.PlotCommit
import org.eclipse.jgit.revplot.PlotLane
import java.text.DateFormat

/*
    CommitDiffModel
 */

class GvCommit(private val repo: Repository,
               private val thisCommit: PlotCommit<PlotLane>,
               private val commitList: GvCommitList,
               private val prevCommit: GvCommit?) {

    //以下のプロパティは、インスタンス生成後に外部から設定する

    //次のコミット
    private var nextCommit: GvCommit? = null

    //ローカルブランチとのリンク
    val localBranches: MutableList<GvLocalBranch> = mutableListOf()

    //リモートブランチとのリンク
    val remoteBranches: MutableList<GvRemoteBranch> = mutableListOf()

    //タグ、当面は名称のみ
    val tags: MutableList<String> = mutableListOf()

    val revCommit: PlotCommit<PlotLane> get() = thisCommit
    val id: ObjectId get() = thisCommit.id
    val fullMessage : String get() = thisCommit.fullMessage
    val shortMessage: String get() = thisCommit.shortMessage
    val committer: String get() = thisCommit.committerIdent.name
    val author: String get() = thisCommit.authorIdent.name
    val laneNumber: Int get() = thisCommit.lane.position

    val isHead : Boolean = ( thisCommit.id == commitList.headId )
    val isMerge: Boolean = ( thisCommit.parentCount > 1 )
    val commitTime: String = ( DateFormat.getDateTimeInstance().format(thisCommit.committerIdent.getWhen()) )

    init {
        prevCommit?.nextCommit = this
    }

    //親コミットの一覧を取得する
    private val parents: List<GvCommit> by lazy {
        thisCommit.parents.mapNotNull { commitList.commitIdMap[it.id] }
    }

    //通過レーン(このコミットの前後でつながるレーン)
    val passThroughLanes : MutableList<Int> by lazy {
        val result = mutableSetOf<PlotLane>()
        commitList.plotCommitList.findPassingThrough(thisCommit, result)
        result.map { it.position }.toMutableList()
    }

    //このコミットから出るレーン
    val exitingLanes: MutableList<Int> by lazy {
        (prevCommit?.enteringLanes?.plus(prevCommit.passThroughLanes)?.minus(passThroughLanes.toSet())
            ?: emptyList()).toMutableList()
    }

    //このコミットに来るレーン
    val enteringLanes : List<Int> by lazy {
        if(thisCommit.parentCount > 1) {
            ( parents.map { it.laneNumber } ).plus( laneNumber ).distinct()
        } else {
            listOf( laneNumber )
        }
    }

    //このコミットのレーン幅
    val maxLaneNumber: Int get() {
        return (passThroughLanes + exitingLanes + enteringLanes).maxOfOrNull { it } ?: -1
    }

    //更新ファイルのリスト
    val diffEntries: List<GviewGitFileEntryModel> by lazy {
        val tree1 = if( thisCommit.parentCount > 0 ) thisCommit.getParent(0).tree else null
        val tree2 = thisCommit.tree
        ByteArrayDiffFormatter(repo).use { fmt ->
            fmt.scan(tree1, tree2).map { GviewGitDiffEntryModel(fmt, it) }
        }
    }

    //タグ検索
    fun containsTag(tagName: String): Boolean = tags.contains(tagName)

    //コメント検索
    fun containsInComment(comment: String): Boolean = fullMessage.contains(comment)
}
