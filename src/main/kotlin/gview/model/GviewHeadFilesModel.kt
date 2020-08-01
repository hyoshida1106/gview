package gview.model

import gview.model.commit.GviewGitFileEntryModel
import gview.model.util.ByteArrayDiffFormatter
import javafx.beans.property.ObjectProperty
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
class GviewHeadFilesModel(private val repositoryProperty: ObjectProperty<Repository>,
                          private val headIdProperty: ObjectProperty<ObjectId?>) {

    //ステージングされているファイルを保持するリスト
    val stagedFilesProperty: ObjectProperty<List<GviewGitFileEntryModel>>
    val stagedFiles: List<GviewGitFileEntryModel> get() { return stagedFilesProperty.value }

    //ワーキングツリー上のインデックス未登録ファイルを保持するリスト
    val changedFilesProperty: ObjectProperty<List<GviewGitFileEntryModel>>
    val changedFiles: List<GviewGitFileEntryModel> get() { return changedFilesProperty.value }

    //ヘッダID
    val headerId: ObjectId? get() { return headIdProperty.value }

    // 対象ファイルがひとつもなければtrueを返す
    fun isEmpty(): Boolean = stagedFiles.isEmpty() && changedFiles.isEmpty()

    //初期化
    init {
        stagedFilesProperty  = SimpleObjectProperty<List<GviewGitFileEntryModel>>()
        changedFilesProperty = SimpleObjectProperty<List<GviewGitFileEntryModel>>()
        headIdProperty.addListener { _, _, newId -> update(newId) }
    }

    //データ更新
    private fun update(head: ObjectId?) {
        if(head != null) {
            val repository = repositoryProperty.value
            val cache = repository.lockDirCache()
            try {
                val iterator = DirCacheIterator(cache)
                val formatter = ByteArrayDiffFormatter(repository)
                stagedFilesProperty.value = getStagedFiles(repository, formatter, iterator, head)
                changedFilesProperty.value = getChangedFiles(repository, formatter, iterator)
            } finally {
                cache.unlock()
            }

        } else {
            stagedFilesProperty.value = listOf()
            changedFilesProperty.value = listOf()
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
