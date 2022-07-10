package gview.model.commit

import gview.model.util.ByteArrayDiffFormatter
import org.eclipse.jgit.diff.DiffEntry

/**
 * 更新ファイル
 */
class GvModifiedFile(private val formatter: ByteArrayDiffFormatter, private val entry: DiffEntry) : GvCommitFile() {

    /**
     * 更新タイプ
     */
    override val type = when (entry.changeType) {
        DiffEntry.ChangeType.ADD -> ModifiedType.ADD
        DiffEntry.ChangeType.COPY -> ModifiedType.COPY
        DiffEntry.ChangeType.DELETE -> ModifiedType.DELETE
        DiffEntry.ChangeType.MODIFY -> ModifiedType.MODIFY
        DiffEntry.ChangeType.RENAME -> ModifiedType.RENAME
        else -> ModifiedType.UNKNOWN
    }

    /**
     * ファイルパス
     *
     * 削除時は削除前のパスを返す
     */
    override val path: String =
        if (entry.changeType == DiffEntry.ChangeType.DELETE) entry.oldPath
        else entry.newPath

    /**
     * Diff文字列の取得
     */
    override fun exportDiffText(): ByteArray {
        return formatter.getText(entry)
    }
}
