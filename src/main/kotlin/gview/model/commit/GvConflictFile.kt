package gview.model.commit

import org.eclipse.jgit.dircache.DirCacheEntry

/**
 * コンフリクトファイル
 */
class GvConflictFile(entry: DirCacheEntry) : GvCommitFile() {

    /**
     * 更新タイプ
     */
    override val type: ModifiedType = ModifiedType.CONFLICT

    /**
     * ファイルパス
     */
    override val path: String = entry.pathString

    /**
     * Diff文字列の取得
     */
    override fun exportDiffText(): ByteArray {
        return ByteArray(0)
    }
}