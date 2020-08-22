package gview.model

import gview.model.commit.GviewGitFileEntryModel
import gview.model.util.ByteArrayDiffFormatter
import javafx.beans.property.SimpleObjectProperty
import org.eclipse.jgit.dircache.DirCacheIterator
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

    //データ更新
    fun update(repository: Repository?, headerId: ObjectId?) {
        if(repository != null && headerId != null) {
            val cache = repository.lockDirCache()
            try {
                val iterator = DirCacheIterator(cache)
                val formatter = ByteArrayDiffFormatter(repository)
                stagedFilesProperty.value = getStagedFiles(repository, formatter, iterator, headerId)
                changedFilesProperty.value = getChangedFiles(repository, formatter, iterator)
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
}
