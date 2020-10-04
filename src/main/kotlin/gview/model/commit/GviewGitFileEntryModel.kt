package gview.model.commit

import gview.model.util.ByteArrayDiffFormatter
import org.eclipse.jgit.diff.DiffEntry

class GviewGitFileEntryModel(
        private val formatter: ByteArrayDiffFormatter,
        private val entry: DiffEntry) {

    //ファイルの更新状況を表すenum
    enum class ModifiedType { ADD, COPY, DELETE, MODIFY, RENAME, UNKNOWN }

    //更新状態を取得する
    val type: ModifiedType = when (entry.changeType) {
        DiffEntry.ChangeType.ADD    -> ModifiedType.ADD
        DiffEntry.ChangeType.COPY   -> ModifiedType.COPY
        DiffEntry.ChangeType.DELETE -> ModifiedType.DELETE
        DiffEntry.ChangeType.MODIFY -> ModifiedType.MODIFY
        DiffEntry.ChangeType.RENAME -> ModifiedType.RENAME
        else -> ModifiedType.UNKNOWN
    }

    //更新状態を文字列として取得する
    val typeName: String = when (type) {
        ModifiedType.ADD     -> "新規"
        ModifiedType.COPY    -> "コピー"
        ModifiedType.DELETE  -> "削除"
        ModifiedType.MODIFY  -> "修正"
        ModifiedType.RENAME  -> "名称変更"
        ModifiedType.UNKNOWN -> "???"
    }

    //ファイルのパス、削除時は削除前のパスを返す
    val path: String = if(type == ModifiedType.DELETE) entry.oldPath else entry.newPath

    //DIFFテキストを取得する
    fun exportDiffText(): ByteArray {
        return formatter.getText(entry)
    }

}