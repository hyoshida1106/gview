package gview.model.workfile

import gview.view.dialog.InformationDialog
import gview.model.GvRepository
import gview.model.commit.GviewConflictEntryModel
import gview.model.commit.GviewGitDiffEntryModel
import gview.model.commit.GviewGitFileEntryModel
import gview.model.util.ByteArrayDiffFormatter
import gview.model.util.ModelObservable
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.dircache.DirCache
import org.eclipse.jgit.dircache.DirCacheEntry
import org.eclipse.jgit.dircache.DirCacheIterator
import org.eclipse.jgit.lib.*
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.*
import org.eclipse.jgit.treewalk.filter.AndTreeFilter
import org.eclipse.jgit.treewalk.filter.IndexDiffFilter
import org.eclipse.jgit.treewalk.filter.TreeFilter

/*
    ワーキングツリーとインデックスファイルの状態を保持するクラス
 */
class GvWorkFilesModel(private val repository: GvRepository)
    : ModelObservable<GvWorkFilesModel>() {

    //ステージングされているファイルを保持するリスト
    val stagedFiles = mutableListOf<GviewGitFileEntryModel>()

    //ワーキングツリー上のインデックス未登録ファイルを保持するリスト
    val changedFiles = mutableListOf<GviewGitFileEntryModel>()

    //コンフリクトの発生しているファイルのリスト
    val conflictedFiles = mutableListOf<String>()

    //コンフリクトの発生していないファイルを取得するためのフィルタ
    class NotConflictFilter(private val treeIdx: Int) : TreeFilter() {
        override fun include(walker: TreeWalk): Boolean {
            val it = walker.getTree(treeIdx, DirCacheIterator::class.java) ?: return true
            return it.dirCacheEntry?.stage == DirCacheEntry.STAGE_0
        }
        override fun shouldBeRecursive(): Boolean { return false }
        override fun clone(): TreeFilter { return this }
        override fun toString(): String { return "TestFilter($treeIdx)"}
    }

    //データ更新
    fun update() {
        stagedFiles.clear()
        changedFiles.clear()
        conflictedFiles.clear()

//        if(repository.isValid) {
            val cache = repository.jgitRepository.lockDirCache()
            try {
                updateStagedFiles(repository.jgitRepository, cache)
                updateChangedFiles(repository.jgitRepository, cache)
                updateConflictedFiles(cache)
            } finally {
                cache.unlock()
            }
//        }

        fireCallback(this)
    }

    //パラメータ設定済のTreeWalkインスタンスを取得する
    private fun getTreeWalk(repository: Repository): TreeWalk {
        val treeWalk = TreeWalk(repository)
        treeWalk.operationType = TreeWalk.OperationType.CHECKIN_OP
        treeWalk.isRecursive = false
        return treeWalk
    }

    //ステージング済ファイル一覧を取得する
    private fun updateStagedFiles(repository: Repository, cache: DirCache) {

        //SubModuleは当面無視する
        val treeWalk = getTreeWalk(repository)
        treeWalk.addTree(getHeadIterator(repository))
        treeWalk.addTree(DirCacheIterator(cache))
        treeWalk.filter = NotConflictFilter(1)

        ByteArrayDiffFormatter(repository).use() { formatter ->
            DiffEntry.scan(treeWalk).forEach {
                stagedFiles.add(GviewGitDiffEntryModel(formatter, it))
            }
        }
    }

    //修正済ファイル一覧を取得する
    private fun updateChangedFiles(repository: Repository, cache: DirCache) {

        //SubModuleは当面無視する
        val treeWalk = getTreeWalk(repository)
        val dirTreeIterator = DirCacheIterator(cache)
        val fileTreeIterator = FileTreeIterator(repository)
        treeWalk.addTree(dirTreeIterator)
        treeWalk.addTree(fileTreeIterator)
        fileTreeIterator.setDirCacheIterator(treeWalk, 0)
        treeWalk.filter = AndTreeFilter.create(
                NotConflictFilter(0),
                IndexDiffFilter(0, 1))

        ByteArrayDiffFormatter(repository).use() { formatter ->
            /* 1度SCANしないとFormatter内の"source"に情報が設定されないらしい */
            formatter.scan(DirCacheIterator(cache), FileTreeIterator(repository))
            DiffEntry.scan(treeWalk).forEach { changedFiles.add(GviewGitDiffEntryModel(formatter, it)) }
        }
    }

    // ファイルイテレータを取得する内部メソッド
    private fun getHeadIterator(repository: Repository): AbstractTreeIterator {
        val headId = repository.resolve(Constants.HEAD) ?: return EmptyTreeIterator()
        val parser = CanonicalTreeParser()
        val revWalk = RevWalk(repository)
        parser.reset(repository.newObjectReader(), revWalk.parseTree(headId).id)
        revWalk.close()
        return parser
    }

    //コンフリクトファイル一覧を取得する
    private fun updateConflictedFiles(cache: DirCache) {
        val cacheIterator = DirCacheIterator(cache)
        while(!cacheIterator.eof()) {
            if(cacheIterator.dirCacheEntry?.stage == DirCacheEntry.STAGE_1) {
                println("${cacheIterator.dirCacheEntry.length} ${cacheIterator.dirCacheEntry.pathString}")
                conflictedFiles.add(cacheIterator.dirCacheEntry.pathString)
                stagedFiles.add(GviewConflictEntryModel(DirCacheEntry(cacheIterator.dirCacheEntry)))
            }
            cacheIterator.next(1)
        }
    }

    //指定されたファイルをステージ
    fun stageFiles(
            files: List<GviewGitFileEntryModel>) {

        val git = Git(repository.jgitRepository)
        var count = 0
        files.forEach {
            when(it.getType()) {
                GviewGitFileEntryModel.ModifiedType.ADD,
                GviewGitFileEntryModel.ModifiedType.MODIFY,
                GviewGitFileEntryModel.ModifiedType.RENAME -> {
                    git.add().addFilepattern(it.getPath()).call()
                    ++count
                }
                GviewGitFileEntryModel.ModifiedType.DELETE -> {
                    git.rm().addFilepattern(it.getPath()).call()
                    ++count
                }
                /* COPYは存在しないらしい */
                else -> { }
            }
        }
        if(count > 0) {
            InformationDialog("$count ファイルをステージしました").showDialog()
            update()
            repository.commits.refresh()
        }
    }

    //指定されたファイルをアンステージ
    fun unStageFiles(
            files: List<GviewGitFileEntryModel>) {

        if(files.isNotEmpty()) {
            val reset = Git(repository.jgitRepository)
                    .reset()
                    .setRef(Constants.HEAD)
            files.forEach { reset.addPath(it.getPath()) }
            reset.call()
            InformationDialog("${files.size} ファイルをアンステージしました").showDialog()
            update()
            repository.commits.refresh()
        }
    }

    //コミット
    fun commitFiles(
            files: List<GviewGitFileEntryModel>,
            message:String,
            userName:String,
            mailAddr:String) {

        if(files.isNotEmpty()) {
            val commit = Git(repository.jgitRepository)
                    .commit()
                    .setCommitter(userName, mailAddr)
                    .setMessage(message)
            files.forEach { commit.setOnly(it.getPath()) }
            commit.call()
            InformationDialog("${files.size} ファイルをコミットしました").showDialog()
            update()
        }
    }
}
