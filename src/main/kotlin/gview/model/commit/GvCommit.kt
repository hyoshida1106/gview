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

    private var nextCommit: GvCommit? = null

    val localBranches:  MutableList<GvLocalBranch>  = mutableListOf()
    val remoteBranches: MutableList<GvRemoteBranch> = mutableListOf()

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

    private val parents: List<GvCommit> by lazy {
        thisCommit.parents.mapNotNull { commitList.commitIdMap[it.id] }
    }

    private val children: List<GvCommit> by lazy {
        (0 until thisCommit.childCount).mapNotNull { commitList.commitIdMap[thisCommit.getChild(it)] }
    }

    val passThroughLanes : MutableList<Int> by lazy {
        val result = mutableSetOf<PlotLane>()
        commitList.plotCommitList.findPassingThrough(thisCommit, result)
        result.map { it.position }.toMutableList()
    }

    val exitingLanes: MutableList<Int> by lazy {
        (prevCommit?.passThroughLanes?.minus(passThroughLanes.toSet()) ?: emptyList())
            .plus(children.map { if( it.isMerge ) laneNumber else it.laneNumber }).distinct().toMutableList()
    }

    val enteringLanes : List<Int> by lazy {
        (nextCommit?.exitingLanes?.plus(nextCommit!!.passThroughLanes)?.minus(passThroughLanes.toSet())
            ?.plus(parents.filter { !it.exitingLanes.contains(laneNumber)}.map { it.laneNumber }) ?: emptyList())
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
