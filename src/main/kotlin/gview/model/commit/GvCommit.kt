package gview.model.commit

import gview.model.branch.GvLocalBranch
import gview.model.branch.GvRemoteBranch
import gview.model.util.ByteArrayDiffFormatter
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revplot.PlotCommit
import org.eclipse.jgit.revplot.PlotLane
import java.text.DateFormat

/**
 * コミット情報モデル
 *
 * @constructor         プライマリコンストラクタ
 * @param[commit]       JGitコミット情報
 * @param[repo]         リポジトリモデル
 * @param[commitList]   コミットリスト参照
 * @param[prevCommit]   ひとつ前のコミット情報
 */
class GvCommit(
    val commit: PlotCommit<PlotLane>,
    private val repo: Repository,
    private val commitList: GvCommitList,
    private val prevCommit: GvCommit?
) {
    /**
     * ひとつ後のコミット情報
     */
    private var nextCommit: GvCommit? = null

    /**
     * このコミットを示しているローカルブランチのリスト
     */
    val localBranches = mutableListOf<GvLocalBranch>()

    /**
     * このコミットを示しているリモートブランチのリスト
     */
    val remoteBranches = mutableListOf<GvRemoteBranch>()

    /**
     * このコミットを示すタグのリスト
     */
    val tags = mutableListOf<String>()

    /**
     * コミットID
     */
    val id: ObjectId = commit.id

    /**
     * メッセージ文字列
     */
    val fullMessage: String = commit.fullMessage

    /**
     * メッセージ文字列(短縮形式)
     */
    val shortMessage: String = commit.shortMessage

    /**
     * コミット実施者
     */
    val committer: String = commit.committerIdent.name

    /**
     *  所有者
     */
    val author: String = commit.authorIdent.name

    /**
     * コミットのレーン番号(0～)
     */
    val laneNumber: Int = commit.lane.position

    /**
     * HEADの場合、trueを返す
     */
    val isHead: Boolean = (commit.id == commitList.headId)

    /**
     * マージコミットの場合、trueを返す
     */
    val isMerge: Boolean = (commit.parentCount > 1)

    /**
     * コミット時間を文字列として取得する
     */
    val commitTime: String = (DateFormat.getDateTimeInstance().format(commit.committerIdent.getWhen()))

    /**
     * 初期化処理
     */
    init {
        //前のインスタンスの「次のインスタンス」に自身を登録
        prevCommit?.nextCommit = this
    }

    /**
     * 親(祖先)コミットのリスト
     */
    private val parents: List<GvCommit> by lazy {
        commit.parents.mapNotNull { commitList.commitIdMap[it.id] }
    }

    /**
     * 子(子孫)コミットのリスト
     */
    private val children: List<GvCommit> by lazy {
        (0 until commit.childCount).mapNotNull { commitList.commitIdMap[commit.getChild(it)] }
    }

    /**
     * 通過レーンのリスト
     */
    val passThroughLanes: List<Int> by lazy {
        val result = mutableSetOf<PlotLane>()
        commitList.plotCommitList.findPassingThrough(commit, result)
        result.map { it.position }
    }

    /**
     * 分岐(このコミットから出る)レーンのリスト
     */
    val exitingLanes: List<Int> by lazy {
        (prevCommit?.passThroughLanes?.minus(passThroughLanes.toSet()) ?: emptyList())
            .plus(children.map { if (it.isMerge) laneNumber else it.laneNumber }).distinct()
    }

    /**
     * 収束(このコミットに入る)レーンのリスト
     */
    val enteringLanes: List<Int> by lazy {
        (nextCommit?.exitingLanes?.plus(nextCommit!!.passThroughLanes)?.minus(passThroughLanes.toSet())
            ?.plus(parents.filter { !it.exitingLanes.contains(laneNumber) }.map { it.laneNumber }) ?: emptyList())
    }

    /**
     * このコミットのレーン数最大値
     */
    val maxLaneNumber: Int by lazy { ( passThroughLanes + exitingLanes + enteringLanes).maxOfOrNull { it } ?: -1 }

    /**
     * HEADへのラインが通過する場合、そのレーン番号。
     * 通過しない場合は -1
     */
    var headerLane: Int = -1

    /**
     * 更新ファイルのリスト
     */
    val diffEntries: List<GvCommitFile> by lazy {
        val tree1 = if (commit.parentCount > 0) commit.getParent(0).tree else null
        val tree2 = commit.tree
        ByteArrayDiffFormatter(repo).use { fmt -> fmt.scan(tree1, tree2).map { GvModifiedFile(fmt, it) } }
    }

    /**
     *  タグ名称検索
     *
     *  措定されたタグ名がこのコミットに含まれているかチェックする
     *  @param[tagName]     検索するタグ名
     *  @return             タグが含まれていればtrueを返す
     */
    fun containsTag(tagName: String): Boolean = tags.contains(tagName)

    /**
     * コメント検索
     *
     *  措定された文字列がこのコミットのコメントに含まれているかチェックする
     *  @param[comment]     検索する文字列
     *  @return             文字列が含まれていればtrueを返す
     */
    fun containsInComment(comment: String): Boolean = fullMessage.contains(comment)
}
