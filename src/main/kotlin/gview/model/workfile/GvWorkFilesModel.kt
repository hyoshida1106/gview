package gview.model.workfile

import gview.model.GvRepository
import gview.model.commit.GvCommitFile
import gview.model.commit.GvConflictFile
import gview.model.commit.GvModifiedFile
import gview.model.util.ByteArrayDiffFormatter
import gview.resourceBundle
import gview.view.dialog.InformationDialog
import javafx.beans.property.SimpleObjectProperty
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.dircache.DirCache
import org.eclipse.jgit.dircache.DirCacheEntry
import org.eclipse.jgit.dircache.DirCacheIterator
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.*
import org.eclipse.jgit.treewalk.filter.AndTreeFilter
import org.eclipse.jgit.treewalk.filter.IndexDiffFilter
import org.eclipse.jgit.treewalk.filter.TreeFilter

/**
 *  ワーキングツリーとインデックスファイルの状態を保持するクラス
 */
class GvWorkFilesModel(private val repository: GvRepository) {

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
    val conflictedFiles = SimpleObjectProperty<List<GvCommitFile>>()

    /**
     * コンフリクトの発生していないファイルを取得するためのフィルタ
     *
     * @param[treeIdx]          ツリーインデックス
     */
    inner class NonconflictingFileFilter(private val treeIdx: Int) : TreeFilter() {
        override fun include(walker: TreeWalk): Boolean {
            val it = walker.getTree(treeIdx, DirCacheIterator::class.java) ?: return true
            return it.dirCacheEntry?.stage == DirCacheEntry.STAGE_0
        }
        override fun shouldBeRecursive(): Boolean { return false }
        override fun clone(): TreeFilter { return this }
    }

    /**
     * 初期化
     */
    init {
        update()
    }

    /**
     * データ更新
     */
    private fun update() {
        val cache = repository.jgitRepository.lockDirCache()
        try {
            updateStagedFiles(repository.jgitRepository, cache)
            updateChangedFiles(repository.jgitRepository, cache)
            updateConflictedFiles(cache)
        } finally {
            cache.unlock()
        }
    }

    /**
     * パラメータ設定済のTreeWalkインスタンスを取得する
     *
     * @param[repository]       リポジトリインスタンス
     * @return                  取得したTreeWalkインスタンス
     */
    private fun getTreeWalk(repository: Repository): TreeWalk {
        val treeWalk = TreeWalk(repository)
        treeWalk.operationType = TreeWalk.OperationType.CHECKIN_OP
        treeWalk.isRecursive = false
        return treeWalk
    }

    /**
     * ステージング済ファイル一覧を取得する
     *
     * @param[repository]       リポジトリインスタンス
     * @param[cache]            ディレクトリキャッシュ
     */
    private fun updateStagedFiles(repository: Repository, cache: DirCache) {
        //SubModuleは当面無視する
        val treeWalk = getTreeWalk(repository)
        treeWalk.addTree(getHeadIterator(repository))
        treeWalk.addTree(DirCacheIterator(cache))
        treeWalk.filter = NonconflictingFileFilter(1)
        ByteArrayDiffFormatter(repository).use { formatter ->
            stagedFiles.value = DiffEntry.scan(treeWalk).map { GvModifiedFile(formatter, it) }
        }
    }

    /**
     * 更新ファイル一覧を取得する
     *
     * @param[repository]       リポジトリインスタンス
     * @param[cache]            ディレクトリキャッシュ
     */
    private fun updateChangedFiles(repository: Repository, cache: DirCache) {
        //SubModuleは当面無視する
        val treeWalk = getTreeWalk(repository)
        val dirTreeIterator = DirCacheIterator(cache)
        val fileTreeIterator = FileTreeIterator(repository)
        treeWalk.addTree(dirTreeIterator)
        treeWalk.addTree(fileTreeIterator)
        fileTreeIterator.setDirCacheIterator(treeWalk, 0)
        treeWalk.filter = AndTreeFilter.create(
            NonconflictingFileFilter(0),
            IndexDiffFilter(0, 1)
        )
        ByteArrayDiffFormatter(repository).use { formatter ->
            // 1度SCANしないとFormatter内の"source"に情報が設定されないらしい
            formatter.scan(DirCacheIterator(cache), FileTreeIterator(repository))
            changedFiles.value = DiffEntry.scan(treeWalk).map { GvModifiedFile(formatter, it) }
        }
    }

    /**
     * ファイルイテレータを取得する内部メソッド
     *
     * @param[repository]       リポジトリインスタンス
     * @return                  TreeIteratorインスタンス
     */
    private fun getHeadIterator(repository: Repository): AbstractTreeIterator {
        val headId = repository.resolve(Constants.HEAD) ?: return EmptyTreeIterator()
        val parser = CanonicalTreeParser()
        val revWalk = RevWalk(repository)
        parser.reset(repository.newObjectReader(), revWalk.parseTree(headId).id)
        revWalk.close()
        return parser
    }

    /**
     * コンフリクトファイル一覧を取得する
     *
     * @param[cache]            ディレクトリキャッシュ
     */
    private fun updateConflictedFiles(cache: DirCache) {
        val files = mutableListOf<GvConflictFile>()
//        val cacheIterator = DirCacheIterator(cache)
//        while (!cacheIterator.eof()) {
//            files.add(GvConflictFile(DirCacheEntry(cacheIterator.dirCacheEntry)))
//            cacheIterator.next(1)
//        }
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

        val git = Git(repository.jgitRepository)
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
        if(addCount > 0 || delCount > 0) {
            if(addCount > 0) addCommand.call()
            if(delCount > 0) delCommand.call()
            InformationDialog(resourceBundle().getString("WorkFileStagedMessage").format(addCount + delCount)).showDialog()
            update()
            repository.commits.refresh()
        }
    }

    /**
     * 指定されたファイルをアンステージングする
     *
     * @param[files]    対象ファイルのリスト
     */
    fun unStageFiles(files: List<GvCommitFile>) {
        if (files.isNotEmpty()) {
            val reset = Git(repository.jgitRepository)
                .reset()
                .setRef(Constants.HEAD)
            files.forEach { reset.addPath(it.path) }
            reset.call()
            InformationDialog(resourceBundle().getString("WorkFilesUnstagedMessage").format(files.size)).showDialog()
            update()
            repository.commits.refresh()
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
            val commit = Git(repository.jgitRepository)
                .commit()
                .setCommitter(userName, mailAddress)
                .setMessage(message)
            files.forEach { commit.setOnly(it.path) }
            commit.call()
            InformationDialog(resourceBundle().getString("workFileCommitMessage").format(files.size)).showDialog()
            update()
        }
    }
}
