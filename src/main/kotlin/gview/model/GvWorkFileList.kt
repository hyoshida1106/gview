package gview.model

import gview.resourceBundle
import gview.view.dialog.InformationDialog
import javafx.beans.property.SimpleObjectProperty
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.dircache.DirCache
import org.eclipse.jgit.dircache.DirCacheEntry
import org.eclipse.jgit.dircache.DirCacheIterator
import org.eclipse.jgit.events.RepositoryEvent
import org.eclipse.jgit.events.RepositoryListener
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.treewalk.filter.SkipWorkTreeFilter

/**
 *  ワーキングツリーとインデックスファイルの状態を保持するクラス
 */
class GvWorkFileList(private val repository: GvRepository) {

    fun interface WorkFileChangedListener : RepositoryListener {
        fun onWorkFileChanged(event: WorkFileChangedEvent)
    }

    class WorkFileChangedEvent : RepositoryEvent<WorkFileChangedListener>() {
        override fun getListenerType(): Class<WorkFileChangedListener> {
            return WorkFileChangedListener::class.java
        }

        override fun dispatch(listener: WorkFileChangedListener?) {
            listener?.onWorkFileChanged(this)
        }
    }

    /**
     * ステージングされているファイルを保持するリスト
     */
    val stagedFiles = SimpleObjectProperty<List<GvCommitFile>>()

    /**
     * ワーキングツリー上のインデックス未登録ファイルを保持するリスト
     */
    val changedFiles = SimpleObjectProperty<List<GvCommitFile>>()

    /**
     * コンフリクトの発生しているファイルのリスト
     */
    private val conflictedFiles = SimpleObjectProperty<List<GvCommitFile>>()

    /*
     * コンフリクトの発生していないファイルを取得するためのフィルタ
     *
     * @param[treeIdx]          ツリーインデックス
     */
//    inner class NonconflictingFileFilter(private val treeIdx: Int) : TreeFilter() {
//        override fun include(walker: TreeWalk): Boolean {
//            val it = walker.getTree(treeIdx, DirCacheIterator::class.java) ?: return true
//            return it.dirCacheEntry?.stage == DirCacheEntry.STAGE_0
//        }
//        override fun shouldBeRecursive(): Boolean { return true }
//        override fun clone(): TreeFilter { return this }
//    }

    /**
     * 初期化
     */
    init {
        updateModel()
        repository.addWorkFileChangedListener { updateModel() }
    }

    /**
     * データ更新
     */
    private fun updateModel() {
        val cache = repository.lockDirCache()
        try {
            updateStagedFiles(repository, cache)
            updateChangedFiles(repository, cache)
            updateConflictedFiles(cache)
        } finally {
            cache.unlock()
        }
    }

    /**
     * ステージング済ファイル一覧を取得する
     *
     * @param[repository]       リポジトリインスタンス
     * @param[cache]            ディレクトリキャッシュ
     */
    private fun updateStagedFiles(repository: GvRepository, cache: DirCache) {
        repository.getTreeWalk().use { treeWalk ->
            val headRevTree = repository.getRevWalk().use { it.parseTree(repository.head) }
            treeWalk.addTree(headRevTree)
            treeWalk.addTree(DirCacheIterator(cache))
            treeWalk.filter = SkipWorkTreeFilter(1)
            repository.getDiffFormatter().use { formatter ->
                stagedFiles.value = DiffEntry.scan(treeWalk).map { GvModifiedFile(formatter, it) }
            }
        }
    }

    /**
     * 更新ファイル一覧を取得する
     *
     * @param[repository]       リポジトリインスタンス
     * @param[cache]            ディレクトリキャッシュ
     */
    private fun updateChangedFiles(repository: GvRepository, cache: DirCache) {
        repository.getTreeWalk().use { treeWalk ->
            val fileTreeIterator = repository.getFileTreeIterator()
            treeWalk.addTree(DirCacheIterator(cache))
            treeWalk.addTree(fileTreeIterator)
            fileTreeIterator.setDirCacheIterator(treeWalk, 0)
            treeWalk.filter = SkipWorkTreeFilter(0)
            repository.getDiffFormatter().use { formatter ->
                changedFiles.value = DiffEntry.scan(treeWalk).map { GvModifiedFile(formatter, it) }
            }
        }
    }

    /**
     * コンフリクトファイル一覧を取得する
     *
     * @param[cache]            ディレクトリキャッシュ
     */
    private fun updateConflictedFiles(cache: DirCache) {
        val files = mutableListOf<GvConflictFile>()
        val cacheIterator = DirCacheIterator(cache)
        while (!cacheIterator.eof()) {
            val dirCacheEntry = cacheIterator.dirCacheEntry
            if (dirCacheEntry != null) {
                files.add(GvConflictFile(DirCacheEntry(dirCacheEntry)))
            }
            cacheIterator.next(1)
        }
        conflictedFiles.value = files
    }

    /**
     * 指定されたファイルをステージングする
     *
     * @param[files]    対象ファイルのリスト
     */
    fun stageFiles(files: List<GvCommitFile>) {
        var addCount = 0
        var delCount = 0

        val git = repository.gitCommand
        val addCommand = git.add()
        val delCommand = git.rm()

        files.forEach {
            when (it.type) {
                GvCommitFile.ModifiedType.ADD,
                GvCommitFile.ModifiedType.MODIFY,
                GvCommitFile.ModifiedType.RENAME -> {
                    addCommand.addFilepattern(it.path)
                    ++addCount
                }
                GvCommitFile.ModifiedType.DELETE -> {
                    delCommand.addFilepattern(it.path)
                    ++delCount
                }
                /* COPYは存在しないらしい */
                else -> {}
            }
        }
        if (addCount > 0 || delCount > 0) {
            if (addCount > 0) addCommand.call()
            if (delCount > 0) delCommand.call()
            repository.workFileChanged()
            InformationDialog(
                resourceBundle().getString("Message.WorkFileStage").format(addCount + delCount)
            ).showDialog()
        }
    }

    /**
     * 指定されたファイルをアンステージングする
     *
     * @param[files]    対象ファイルのリスト
     */
    fun unStageFiles(files: List<GvCommitFile>) {
        if (files.isNotEmpty()) {
            val reset = repository.gitCommand
                .reset()
                .setRef(Constants.HEAD)
            files.forEach { reset.addPath(it.path) }
            reset.call()
            repository.workFileChanged()
            InformationDialog(resourceBundle().getString("Message.WorkFileUnStage").format(files.size)).showDialog()
        }
    }

    /**
     * 指定されたファイルをコミットする
     *
     * @param[files]    対象ファイルのリスト
     * @param[message]  コミットメッセージ
     * @param[userName] コミットするユーザ名
     * @param[mailAddress] コミットするユーザのメールアドレス
     */
    fun commitFiles(files: List<GvCommitFile>, message: String, userName: String, mailAddress: String) {
        if (files.isNotEmpty()) {
            val commit = repository.gitCommand
                .commit()
                .setCommitter(userName, mailAddress)
                .setMessage(message)
            files.forEach { commit.setOnly(it.path) }
            commit.call()
            repository.branchChanged()
            InformationDialog(resourceBundle().getString("Message.WorkFileCommit").format(files.size)).showDialog()
        }
    }

    fun discardFiles(files: List<GvCommitFile>) {
        var restoreCount = 0
        val checkoutCommand = repository.gitCommand.checkout()

        files.forEach {
            when (it.type) {
                GvCommitFile.ModifiedType.MODIFY,
                GvCommitFile.ModifiedType.DELETE,
                GvCommitFile.ModifiedType.RENAME -> {
                    checkoutCommand.addPath(it.path)
                    ++restoreCount
                }
                else -> {}
            }
        }
        if (restoreCount > 0) {
            checkoutCommand.call()
            repository.workFileChanged()
            InformationDialog(
                resourceBundle().getString("Message.WorkFileRestore").format(restoreCount)
            ).showDialog()
        }
    }
}