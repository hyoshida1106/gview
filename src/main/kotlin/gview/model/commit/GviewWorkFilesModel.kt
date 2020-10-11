package gview.model.commit

import gview.gui.dialog.InformationDialog
import gview.model.GviewRepositoryModel
import gview.model.util.ByteArrayDiffFormatter
import gview.model.util.ModelObservable
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.dircache.DirCache
import org.eclipse.jgit.dircache.DirCacheIterator
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.FileMode
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.FileTreeIterator

/*
    ワーキングツリーとインデックスファイルの状態を保持するクラス
 */
class GviewWorkFilesModel(
        private val repository: GviewRepositoryModel)
    : ModelObservable<GviewWorkFilesModel>() {

    //ステージングされているファイルを保持するリスト
    val stagedFiles = mutableListOf<GviewGitFileEntryModel>()

    //ワーキングツリー上のインデックス未登録ファイルを保持するリスト
    val changedFiles =  mutableListOf<GviewGitFileEntryModel>()

    //データ更新
    fun update() {
        stagedFiles.clear()
        changedFiles.clear()

        val jgitRepository = repository.jgitRepository
        if(jgitRepository != null) {
            val cache = jgitRepository.lockDirCache()
            try {
                updateStagedFiles(jgitRepository, cache, stagedFiles)
                updateChangedFiles(jgitRepository, cache, changedFiles)
            } finally {
                cache.unlock()
            }
        }

        fireCallback(this)
    }

    //ステージング済ファイル一覧を取得する
    private fun updateStagedFiles(
            repository: Repository,
            cache: DirCache,
            files: MutableList<GviewGitFileEntryModel>) {

        //SubModuleは当面無視する
        val headId = repository.resolve(Constants.HEAD)
        ByteArrayDiffFormatter(repository).use() { formatter ->
            formatter.scan(toTreeIterator(repository, headId), DirCacheIterator(cache))
                    .filter { it.oldMode != FileMode.GITLINK && it.newMode != FileMode.GITLINK }
                    .forEach { files.add(GviewGitFileEntryModel(formatter, it)) }
        }
    }

    //修正済ファイル一覧を取得する
    private fun updateChangedFiles(
            repository: Repository,
            cache: DirCache,
            files: MutableList<GviewGitFileEntryModel>) {

        //SubModuleは当面無視する
        val cacheIterator = DirCacheIterator(cache)
        ByteArrayDiffFormatter(repository).use() { formatter ->
            formatter.scan(cacheIterator, FileTreeIterator(repository))
                    .filter { it.oldMode != FileMode.GITLINK && it.newMode != FileMode.GITLINK }
                    .forEach { files.add(GviewGitFileEntryModel(formatter, it)) }
        }
    }

    // ファイルイテレータを取得する内部メソッド
    private fun toTreeIterator(
            repository: Repository,
            id: ObjectId): AbstractTreeIterator {

        val parser = CanonicalTreeParser()
        val revWalk = RevWalk(repository)
        parser.reset(repository.newObjectReader(), revWalk.parseTree(id).id)
        revWalk.close()
        return parser
    }

    //指定されたファイルをステージ
    fun stageFiles(
            files: List<GviewGitFileEntryModel>) {

        val git = Git(repository.jgitRepository)
        var count = 0
        files.forEach {
            when(it.type) {
                GviewGitFileEntryModel.ModifiedType.ADD,
                GviewGitFileEntryModel.ModifiedType.MODIFY,
                GviewGitFileEntryModel.ModifiedType.RENAME -> {
                    git.add().addFilepattern(it.path).call()
                    ++count
                }
                GviewGitFileEntryModel.ModifiedType.DELETE -> {
                    git.rm().addFilepattern(it.path).call()
                    ++count
                }
                /* COPYは存在しないらしい */
                else -> { }
            }
        }
        if(count > 0) {
            InformationDialog("$count ファイルをステージしました").showDialog()
            update()
            repository.branches.commits.refresh()
        }
    }

    //指定されたファイルをアンステージ
    fun unStageFiles(
            files: List<GviewGitFileEntryModel>) {

        if(files.isNotEmpty()) {
            val reset = Git(repository.jgitRepository)
                    .reset()
                    .setRef(Constants.HEAD)
            files.forEach { reset.addPath(it.path) }
            reset.call()
            InformationDialog("${files.size} ファイルをアンステージしました").showDialog()
            update()
            repository.branches.commits.refresh()
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
            files.forEach { commit.setOnly(it.path) }
            commit.call()
            InformationDialog("${files.size} ファイルをコミットしました").showDialog()
            update()
            repository.branches.update()
        }
    }
}
