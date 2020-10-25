package gview.model.commit

import gview.model.branch.GviewLocalBranchModel
import gview.model.branch.GviewRemoteBranchModel
import gview.model.util.ByteArrayDiffFormatter
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revplot.PlotCommit
import org.eclipse.jgit.revplot.PlotLane
import org.eclipse.jgit.revwalk.RevCommit
import java.text.DateFormat

/*
    CommitDiffModel
 */
class GviewCommitDataModel(private val repo: Repository,
                           private val commitList: GviewCommitListModel,
                           private val commit: PlotCommit<PlotLane>,
                           private val prevCommit: GviewCommitDataModel?) {

    //以下のプロパティは、インスタンス生成後に外部から設定する

    //次のコミット
    var nextCommit: GviewCommitDataModel? = null

    //ローカルブランチとのリンク
    val localBranches: MutableList<GviewLocalBranchModel> = mutableListOf()

    //リモートブランチとのリンク
    val remoteBranches: MutableList<GviewRemoteBranchModel> = mutableListOf()

    //タグ、当面は名称のみ
    val tags: MutableList<String> = mutableListOf()

    init {
        if(prevCommit != null) prevCommit.nextCommit = this
    }

    // ID
    val id: ObjectId = commit.id

    //　HEADであればtrue
    val isHead: Boolean = (id == commitList.headId)

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

    // RevCommit instance
    val revCommit: RevCommit = commit

    //親コミットの一覧を取得する
    private val parents: List<GviewCommitDataModel> by lazy {
        commit.parents.mapNotNull { commitList.commitIdMap[it.id] }
    }

    //通過レーン(このコミットの前後でつながるレーン)
    val passLanes : MutableList<Int> by lazy {
        val result = mutableSetOf<PlotLane>()
        commitList.plotCommitList.findPassingThrough(commit, result)
        result.map { it.position }.sorted().distinct().toMutableList()
    }

    //このコミットから出るレーン
    val exitTo : MutableList<Int> by lazy {
        //１つ先のコミット情報をチェックする
        if(prevCommit != null) {
            val exitLanes = mutableListOf<Int>()
            //このコミットを通過しない通過レーンがあれば、分岐する
            exitLanes.addAll(prevCommit.passLanes.filterNot { passLanes.contains(it) })
            //親コミットに自分が含まれていれば、自分のレーンを延長する
            if(prevCommit.parents.contains(this)) {
                if(passLanes.contains(prevCommit.laneNumber)) {
                    exitLanes.add(laneNumber)
                } else {
                    exitLanes.add(prevCommit.laneNumber)
                }
            }
            //ソートして重複を削除する
            exitLanes.sorted().distinct().toMutableList()
        } else {
            //空リスト
            mutableListOf<Int>()
        }
    }

    //このコミットに来るレーン
    val enterFrom : MutableList<Int> by lazy {
        //１つ前のコミット情報をチェックする
        if(nextCommit != null) {
            val enterLanes = mutableListOf<Int>()
            //このコミットを通過しない通過レーンがあれば、マージする
            enterLanes.addAll(nextCommit!!.passLanes.filterNot { passLanes.contains(it) })
            //分岐先が自分の通過レーンに含まれていなければ、マージする
            enterLanes.addAll(nextCommit!!.exitTo.filterNot { passLanes.contains(it) })
            //ソートして重複を削除する
            enterLanes.sorted().distinct().toMutableList()
        } else {
            //末端は真下に線を引く(と自然に見える)
            listOf( laneNumber ).toMutableList()
        }
    }

    //このコミットのレーン幅
    val maxLaneNumber: Int get() {
        return (passLanes + exitTo + enterFrom).maxOfOrNull { it } ?: -1
    }

    //更新ファイルのリスト
    val diffEntries: List<GviewGitFileEntryModel> by lazy {
        val tree1 = if( commit.parentCount > 0 ) commit.getParent(0).tree else null
        val tree2 = commit.tree
        ByteArrayDiffFormatter(repo).use { fmt ->
            fmt.scan(tree1, tree2).map { GviewGitFileEntryModel(fmt, it) }
        }
    }

    //タグ検索
    fun containsTag(tagName: String): Boolean = tags.contains(tagName)

    //コメント検索
    fun containsInComment(comment: String): Boolean = fullMessage.contains(comment)
}
