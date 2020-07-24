package gview.model.commit

import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revplot.PlotCommit
import org.eclipse.jgit.revplot.PlotLane
import java.text.DateFormat


/*
    CommitDiffModel
 */
class CommitDataModel(private val repo: Repository,
                      private val commit: PlotCommit<PlotLane>,
                      private val prev: CommitDataModel?) {

    // ID
    val id : String = ObjectId.toString(commit.id)

    // Commit message (short)
    val shortMessage : String = commit.shortMessage

    // Commit message (full)
    val fullMessage : String = commit.fullMessage

    // Commit Time
    val commitTime : String = DateFormat.getDateTimeInstance().format(commit.committerIdent.getWhen())

    // Committer
    val committer : String = commit.committerIdent.name

    // Author
    val author : String = commit.authorIdent.name

    // Lane Number
    val laneNumber : Int = commit.lane.position

    // true if merge
    val isMerge : Boolean = ( commit.parentCount > 1 )

    // Passways
//    val passWays : MutableList<Int> by lazy {
//        commitList.getPassways(commit).sorted().toMutableList()
//    }

    // Parents
//    private val parents: List<Int> by lazy {
//        List(commit.parentCount) { commitList[commit.getParent(it).id]!!.laneNumber }.sorted()
//    }

    // Children
//    val children: List<Int> by lazy {
//        List(commit.childCount) { commitList[commit.getChild(it).id]!!.laneNumber }.sorted()
//    }

    // BranchTo
//    val branchTo : MutableList<Int> by lazy {
//        val branchLanes = mutableListOf<Int>()
//        if(this.prev != null) {
//            val p: CommitDataModel  = this.prev          // not null
//            p.passWays.forEach {
//                if(!this.passWays.contains(it)) {
//                    branchLanes.add(it)
//                }
//            }
//            if(!this.passWays.contains(p.laneNumber)) {
//                branchLanes.add(p.laneNumber)
//            }
//            if(p.parents.contains(this.laneNumber)) {
//                branchLanes.add(this.laneNumber)
//            }
//        }
//        branchLanes.sorted().distinct().toMutableList()
//    }

    // MergeFrom
//    val mergeFrom : List<Int> by lazy {
//        List<Int>(commit.parentCount) {
//            val c = commitList[commit.getParent(it).id]
//            if(c?.branchTo?.contains(this.laneNumber) != true) {
//                c!!.laneNumber
//            } else {
//                this.laneNumber
//            }
//        }.sorted().distinct()
//    }

//    val entryList: List<DiffEntryModel> by lazy {
//        val tree1 = if( commit.parentCount > 0 ) commit.getParent(0).tree else null
//        val tree2 = commit.tree
//        val fmt = ByteArrayDiffFormatter(repo.repository)
//        fmt.scan(tree1, tree2).map { DiffEntryModel(fmt, it) }
//    }
//
//    fun containsTag(tagName: String): Boolean {
//        for(tag in tags) {
//            if(tag.matches(tagName))
//                return true
//        }
//        return false
//    }

//    fun containsInComment(comment: String): Boolean {
//        return fullMessage.contains(comment)
//    }

//    private fun timeToString(time: Date): String {
//        return DateFormat.getDateTimeInstance().format(time)
//    }
}
