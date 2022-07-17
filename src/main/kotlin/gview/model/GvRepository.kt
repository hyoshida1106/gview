package gview.model

import gview.model.branch.GvBranchList
import gview.model.commit.GvCommitList
import gview.model.workfile.GvWorkFileList
import javafx.beans.property.SimpleObjectProperty
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.dircache.DirCache
import org.eclipse.jgit.events.ListenerHandle
import org.eclipse.jgit.events.RefsChangedListener
import org.eclipse.jgit.lib.*
import org.eclipse.jgit.revplot.PlotWalk
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.*
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.File

/**
 * リポジトリモデル
 *
 * @constructor コンストラクタ
 * @param[jgitRepository] JGitのリポジトリインスタンス
 */
class GvRepository private constructor(private val jgitRepository: Repository) {

    /**
     * 作業ファイル情報
     */
    val workFiles = GvWorkFileList(this)

    fun addWorkFileChangedListener(listener: GvWorkFileList.WorkFileChangedListener) {
        jgitRepository.listenerList.addListener(GvWorkFileList.WorkFileChangedListener::class.java, listener)
    }

    fun workFileChanged() {
        jgitRepository.listenerList.dispatch(GvWorkFileList.WorkFileChangedEvent())
    }

    /**
     * ブランチ情報リスト(リモート/ローカルブランチ)
     */
    val branches = GvBranchList(this)

    fun addBranchChangedListener(listener: GvBranchList.BranchChangedListener) {
        jgitRepository.listenerList.addListener(GvBranchList.BranchChangedListener::class.java, listener)
    }

    fun branchChanged() {
        jgitRepository.listenerList.dispatch(GvBranchList.BranchChangedEvent())
    }

    /**
     * コミット情報リスト
     */
    val commits = GvCommitList(this)

    fun addCommitChangedListener(listener: GvCommitList.CommitChangedListener) {
        jgitRepository.listenerList.addListener(GvCommitList.CommitChangedListener::class.java, listener)
    }

    fun commitChanged() {
        jgitRepository.listenerList.dispatch(GvCommitList.CommitChangedEvent())
    }


    val absolutePath: String get() = jgitRepository.directory.absolutePath

    val currentBranch: String get() = jgitRepository.branch

    val headId: ObjectId? get() = jgitRepository.resolve(Constants.HEAD)

    val config: StoredConfig get() = jgitRepository.config

    val gitCommand: Git get() = Git(jgitRepository)


    fun addRefsChangedListener(listener: RefsChangedListener): ListenerHandle =
        jgitRepository.listenerList.addRefsChangedListener(listener)


    fun shortenRemoteBranchName(name: String): String =
        jgitRepository.shortenRemoteBranchName(name) ?: name

    fun lockDirCache(): DirCache = jgitRepository.lockDirCache()

    fun getTrackingStatus(path: String): BranchTrackingStatus? =
        BranchTrackingStatus.of(jgitRepository, path)

    fun getHeadIterator( ): AbstractTreeIterator {
        if(headId == null) return EmptyTreeIterator()
        val parser = CanonicalTreeParser()
        val revWalk = RevWalk(jgitRepository)
        parser.reset(jgitRepository.newObjectReader(), revWalk.parseTree(headId).id)
        revWalk.close()
        return parser
    }

    fun getFileTreeIterator(): FileTreeIterator =
        FileTreeIterator(jgitRepository)

    fun getPlotWalk(): PlotWalk = PlotWalk(jgitRepository)

    fun getRevWalk(): RevWalk = RevWalk(jgitRepository)

    fun getTreeWalk( ): TreeWalk {
        val treeWalk = TreeWalk(jgitRepository)
        treeWalk.operationType = TreeWalk.OperationType.CHECKIN_OP
        treeWalk.isRecursive = false
        return treeWalk
    }

    fun getDiffFormatter(output: ByteArrayOutputStream = ByteArrayOutputStream()): ByteArrayDiffFormatter =
        ByteArrayDiffFormatter(output)

    inner class ByteArrayDiffFormatter(private val output: ByteArrayOutputStream): DiffFormatter(output), Closeable {
        init {
            super.setRepository(jgitRepository)
        }
        fun getText(entry: DiffEntry): ByteArray {
            output.reset()
            super.format(entry)
            return output.toByteArray()
        }
    }

    /**
     * シングルトン管理のための Companion Object
     */
    companion object {
        /**
         * 現在有効なリポジトリモデルを保持する
         */
        val currentRepositoryProperty = SimpleObjectProperty<GvRepository>()

        /**
         * 現在有効なリポジトリインスタンスを参照するためのプロパティ
         */
        val currentRepository: GvRepository? get() = currentRepositoryProperty.value

        /**
         * リポジトリを新規作成し、カレントリポジトリとする
         *
         * @param[directoryPath]    リポジトリを作成するパス
         * @param[isBare]           Bareリポジトリを生成する場合、trueを指定する
         */
        fun init(directoryPath: String, isBare: Boolean = false) {
            currentRepositoryProperty.set(
                GvRepository(
                    Git.init()
                        .setBare(isBare)
                        .setDirectory(File(directoryPath))
                        .setGitDir(File(directoryPath, ".git"))
                        .call()
                        .repository
                )
            )
        }

        /**
         * ローカルリポジトリをオープンし、カレントリポジトリとする
         *
         * @param[directoryPath]    オープンするディレクトリのパス
         */
        fun open(directoryPath: String) {
            currentRepositoryProperty.set(
                GvRepository(
                    Git.open(File(directoryPath))
                        .repository
                )
            )
        }

        /**
         * リモートリポジトリのクローンを生成し、カレントリポジトリとする
         *
         * @param[directoryPath]    ローカルリポジトリを生成するディレクトリのパス
         * @param[remoteUrl]        クローンするリモートリポジトリのURL
         * @param[isBare]           生成するリポジトリがBareの場合、trueを指定する
         */
        fun clone(directoryPath: String, remoteUrl: String, isBare: Boolean = false) {
            currentRepositoryProperty.set(
                GvRepository(
                    Git.cloneRepository()
                        .setURI(remoteUrl)
                        .setDirectory(File(directoryPath))
                        .setBare(isBare)
                        .call()
                        .repository
                )
            )
        }
    }
}
