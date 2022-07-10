package gview.model.commit

import gview.resourceBundle

/**
 *  コミット対象ファイル情報
 */
abstract class  GvCommitFile {

    /**
     * 更新状態を表す列挙型
     */
    enum class ModifiedType { ADD, COPY, DELETE, MODIFY, RENAME, CONFLICT, UNKNOWN }

    /**
     * 更新タイプ
     */
    abstract val type: ModifiedType

    /**
     * 更新タイプに対応する文字列
     */
    val typeName: String by lazy {  when (type) {
        ModifiedType.ADD        -> resourceBundle().getString("TypeNameNew")
        ModifiedType.COPY       -> resourceBundle().getString("TypeNameCopy")
        ModifiedType.DELETE     -> resourceBundle().getString("TypeNameDelete")
        ModifiedType.MODIFY     -> resourceBundle().getString("TypeNameModify")
        ModifiedType.RENAME     -> resourceBundle().getString("TypeNameRename")
        ModifiedType.CONFLICT   -> resourceBundle().getString("TypeNameConflict")
        else -> "???"
    } }

    /**
     * ファイルパス
     */
    abstract val path: String

    /**
     * Diff文字列の取得
     */
    abstract fun exportDiffText(): ByteArray
}
