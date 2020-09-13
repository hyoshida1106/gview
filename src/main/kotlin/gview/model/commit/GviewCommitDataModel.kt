package gview.model.commit

import gview.model.GviewCommitListModel
import gview.model.branch.GviewLocalBranchModel
import gview.model.branch.GviewRemoteBranchModel
import gview.model.util.ByteArrayDiffFormatter
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revplot.PlotCommit
import org.eclipse.jgit.revplot.PlotLane
import java.text.DateFormat

/*
    CommitDiffModel
 */
class GviewCommitDataModel(private val repo: Repository,
                           private val commitList: GviewCommitListModel,
                           private val commit: PlotCommit<PlotLane>,
                           private val isHead: Boolean,
                           private val prevCommit: GviewCommitDataModel?) {
    // ID
    val id: ObjectId = commit.id

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


    //以下のプロパティは、インスタンス生成後に外部から設定する

    //ローカルブランチとのリンク
    val localBranches: MutableList<GviewLocalBranchModel> = mutableListOf()

    //リモートブランチとのリンク
    val remoteBranches: MutableList<GviewRemoteBranchModel> = mutableListOf()

    //タグ、当面は名称のみ
    val tags: MutableList<String> = mutableListOf()


    //通過パス(このコミットの前後でつながるパス
    val passWays : List<Int> by lazy {
        val result = mutableSetOf<PlotLane>()
        commitList.plotCommitList.findPassingThrough(commit, result)
        result.map { it.position }
    }

    //親コミットの一覧を取得する
    private val parents: List<GviewCommitDataModel> by lazy {
        val list = mutableListOf<GviewCommitDataModel>()
        (0 until commit.parentCount).forEach {
            val parent = commitList.commitMap[commit.getParent(it).id]
            if(parent != null) list.add(parent)
        }
        list
    }

    //このコミットから出るレーン
    val branchTo : List<Int> by lazy {
        val branchLanes = mutableListOf<Int>()
        //１つ先のコミット情報をチェックする
        if(prevCommit != null) {
            //このコミットにない通過レーンがあれば、分岐する必要がある
            prevCommit.passWays.filterNot { passWays.contains(it) }.forEach {
                branchLanes.add(it)
            }
            //レーンがこちらの通過レーンでなければ、分岐する必要がある
            if(!passWays.contains(prevCommit.laneNumber)) {
                branchLanes.add(prevCommit.laneNumber)
            }
            //親コミットに自分が含まれていれば、自分のレーンを延長する
            if(prevCommit.parents.contains(this)) {
                branchLanes.add(laneNumber)
            }
        }

        //コミットがHEADの場合、HEADERへのラインを引いておく
        if(isHead) {
            branchLanes.add(laneNumber)
        }

        //ソートした上で重複を削除する
        branchLanes.sorted().distinct().toMutableList()
    }

    //このコミットに来るレーン
    val mergeFrom : List<Int> by lazy {
        if(parents.count() > 0) {
            //親コミットから出るレーンにつながるように線を引く
            List<Int>(parents.count()) {
                if (parents[it].branchTo.contains(laneNumber)) {
                    laneNumber
                } else {
                    parents[it].laneNumber
                }
            }.sorted().distinct()
        } else {
            //親がひとつもない場合(末端)、真下に線を引く(と自然に見える)
            listOf(laneNumber)
        }
    }

    //更新ファイルのリスト
    val diffEntries: List<GviewGitFileEntryModel> by lazy {
        val tree1 = if( commit.parentCount > 0 ) commit.getParent(0).tree else null
        val tree2 = commit.tree
        ByteArrayDiffFormatter(repo).use() { fmt ->
            fmt.scan(tree1, tree2).map { GviewGitFileEntryModel(fmt, it) }
        }
    }

    //タグ検索
    fun containsTag(tagName: String): Boolean = tags.contains(tagName)

    //コメント検索
    fun containsInComment(comment: String): Boolean = fullMessage.contains(comment)
}
