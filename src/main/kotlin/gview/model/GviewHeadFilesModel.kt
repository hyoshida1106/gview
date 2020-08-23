package gview.model

import gview.gui.framework.GviewCommonDialog
import gview.model.commit.GviewGitFileEntryModel
import gview.model.util.ByteArrayDiffFormatter
import javafx.beans.property.SimpleObjectProperty
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.dircache.DirCacheIterator
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.FileTreeIterator

/*
    ワーキングツリーとインデックスファイルの状態を保持するクラス
 */
class GviewHeadFilesModel() {

    //ステージングされているファイルを保持するリスト
    val stagedFilesProperty = SimpleObjectProperty<List<GviewGitFileEntryModel>?>(null)
    val stagedFiles: List<GviewGitFileEntryModel>? get() { return stagedFilesProperty.value }

    //ワーキングツリー上のインデックス未登録ファイルを保持するリスト
    val changedFilesProperty = SimpleObjectProperty<List<GviewGitFileEntryModel>?>()
    val changedFiles: List<GviewGitFileEntryModel>? get() { return changedFilesProperty.value }

    //現在のリポジトリ、HEAD ID
    private var repository: Repository? = null
    private var headerId: ObjectId? = null

    //データ更新
    fun update(newRepository: Repository?, newHeaderId: ObjectId?) {
        repository = newRepository
        headerId = newHeaderId
        refresh()
    }

    //ヘッダ情報を更新
    private fun refresh() {
        if(repository != null && headerId != null) {
            val repo = repository!!
            val hid  = headerId!!

            val cache = repo.lockDirCache()
            try {
                val iterator = DirCacheIterator(cache)
                val formatter = ByteArrayDiffFormatter(repo)
                stagedFilesProperty.value = getStagedFiles(repo, formatter, iterator, hid)
                changedFilesProperty.value = getChangedFiles(repo, formatter, iterator)
            } finally {
                cache.unlock()
            }
        } else {
            stagedFilesProperty.value = emptyList()
            changedFilesProperty.value = emptyList()
        }
    }

    //ステージング済ファイル一覧を取得する
    private fun getStagedFiles(repository: Repository,
                               formatter: ByteArrayDiffFormatter,
                               cacheIterator: DirCacheIterator,
                               head: ObjectId): List<GviewGitFileEntryModel> {
        cacheIterator.reset()
        return formatter.scan(toTreeIterator(repository, head), cacheIterator).map {
            GviewGitFileEntryModel(formatter, it) }
    }

    //修正済ファイル一覧を取得する
    private fun getChangedFiles(repository: Repository,
                                formatter: ByteArrayDiffFormatter,
                                cacheIterator: DirCacheIterator): List<GviewGitFileEntryModel> {
        cacheIterator.reset()
        return formatter.scan(cacheIterator, FileTreeIterator(repository)).map {
            GviewGitFileEntryModel(formatter, it) }
    }

    // ファイルイテレータを取得する内部メソッド
    private fun toTreeIterator(repository: Repository, id: ObjectId): AbstractTreeIterator {
        val parser = CanonicalTreeParser()
        val revWalk = RevWalk(repository)
        parser.reset(repository.newObjectReader(), revWalk.parseTree(id).id)
        return parser
    }

    //指定されたファイルをステージ
    fun stageFiles(files: List<GviewGitFileEntryModel>) {
        val git = Git(GviewRepositoryModel.currentRepository.jgitRepository)
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
            GviewCommonDialog.informationDialog("$count ファイルをステージしました")
            refresh()
        }
    }

    //指定されたファイルをアンステージ
    fun unStageFiles(files: List<GviewGitFileEntryModel>) {
        if(files.isNotEmpty()) {
            val reset = Git(GviewRepositoryModel.currentRepository.jgitRepository)
                    .reset()
                    .setRef(Constants.HEAD)
            files.forEach { reset.addPath(it.path) }
            reset.call()
            GviewCommonDialog.informationDialog("${files.size} ファイルをアンステージしました")
            refresh()
        }
    }

    //コミット
    fun commitFiles(files: List<GviewGitFileEntryModel>, message:String) {
    }
}
