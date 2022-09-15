package gview.model

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
        ModifiedType.ADD -> resourceBundle().getString("Term.New")
        ModifiedType.COPY -> resourceBundle().getString("Term.Copy")
        ModifiedType.DELETE -> resourceBundle().getString("Term.Delete")
        ModifiedType.MODIFY -> resourceBundle().getString("Term.Modify")
        ModifiedType.RENAME -> resourceBundle().getString("Term.Rename")
        ModifiedType.CONFLICT -> resourceBundle().getString("Term.Conflict")
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
