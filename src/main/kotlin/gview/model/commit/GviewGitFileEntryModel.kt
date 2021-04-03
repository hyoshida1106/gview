package gview.model.commit

import gview.model.util.ByteArrayDiffFormatter
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.dircache.DirCacheEntry

abstract class  GviewGitFileEntryModel {

    //ファイルの更新状況を表すenum
    enum class ModifiedType { ADD, COPY, DELETE, MODIFY, RENAME, CONFLICT, UNKNOWN }

    //更新状態を取得する
    abstract fun getType(): ModifiedType

    //更新状態を文字列として取得する
    fun getTypeName(): String = when (getType()) {
        ModifiedType.ADD        -> "新規"
        ModifiedType.COPY       -> "コピー"
        ModifiedType.DELETE     -> "削除"
        ModifiedType.MODIFY     -> "修正"
        ModifiedType.RENAME     -> "名称変更"
        ModifiedType.CONFLICT   -> "衝突"
        else -> "???"
    }

    //ファイルのパス、削除時は削除前のパスを返す
    abstract fun getPath(): String

    //DIFFテキストを取得する
    abstract fun exportDiffText(): ByteArray
}

class GviewGitDiffEntryModel(private val formatter: ByteArrayDiffFormatter, private val entry: DiffEntry)
    : GviewGitFileEntryModel() {

    //更新状態を取得する
    override fun getType() = when (entry.changeType) {
        DiffEntry.ChangeType.ADD    -> ModifiedType.ADD
        DiffEntry.ChangeType.COPY   -> ModifiedType.COPY
        DiffEntry.ChangeType.DELETE -> ModifiedType.DELETE
        DiffEntry.ChangeType.MODIFY -> ModifiedType.MODIFY
        DiffEntry.ChangeType.RENAME -> ModifiedType.RENAME
        else -> ModifiedType.UNKNOWN
    }

    //ファイルのパス、削除時は削除前のパスを返す
    override fun getPath(): String =
            if(entry.changeType == DiffEntry.ChangeType.DELETE) entry.oldPath
            else entry.newPath

    //DIFFテキストを取得する
    override fun exportDiffText(): ByteArray {
        return formatter.getText(entry)
    }
}

class GviewConflictEntryModel(private val entry: DirCacheEntry): GviewGitFileEntryModel() {

    override fun getType(): ModifiedType = ModifiedType.CONFLICT

    override fun getPath(): String {
        return entry.pathString
    }

    override fun exportDiffText(): ByteArray {
        return ByteArray(0)
    }

}