package gview.model

import gview.model.branch.GvBranchList
import gview.model.commit.GvCommitList
import gview.model.workfile.GvWorkFileList
import javafx.beans.property.SimpleObjectProperty
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.dircache.DirCache
import org.eclipse.jgit.lib.*
import org.eclipse.jgit.revplot.PlotWalk
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.transport.RemoteConfig
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

    val head: ObjectId? get() = jgitRepository.resolve(Constants.HEAD)

    val gitCommand: Git get() = Git(jgitRepository)

    fun shortenRemoteBranchName(name: String): String =
        jgitRepository.shortenRemoteBranchName(name) ?: name

    fun lockDirCache(): DirCache = jgitRepository.lockDirCache()

    fun getTrackingStatus(path: String): BranchTrackingStatus? =
        BranchTrackingStatus.of(jgitRepository, path)

    fun getFileTreeIterator(): FileTreeIterator =
        FileTreeIterator(jgitRepository)

    fun getPlotWalk(): PlotWalk = PlotWalk(jgitRepository)

    fun getRevWalk(): RevWalk = RevWalk(jgitRepository)

    fun getTreeWalk( ): TreeWalk {
        val treeWalk = TreeWalk(jgitRepository)
        treeWalk.operationType = TreeWalk.OperationType.CHECKIN_OP
        treeWalk.isRecursive = true
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

    val remoteConfigList: List<RemoteConfig> get() = RemoteConfig.getAllRemoteConfigs(jgitRepository.config)

    val userConfig: UserConfig get() = jgitRepository.config.get(UserConfig.KEY)

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
            currentRepository?.jgitRepository?.close()
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
            currentRepository?.jgitRepository?.close()
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
            currentRepository?.jgitRepository?.close()
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

        fun fetch(monitor: ProgressMonitor, remote: String? = null, prune: Boolean = false) {
            val repository = currentRepository ?: return
            val remoteName = remote
                ?: if (repository.remoteConfigList.isNotEmpty()) repository.remoteConfigList[0].name else "origin"  // NON-NLS
            repository.gitCommand.fetch()
                .setProgressMonitor(monitor)
                .setRemote(remoteName)
                .setRemoveDeletedRefs(prune)
                .call()
            open(repository.absolutePath)
        }

    }
}
