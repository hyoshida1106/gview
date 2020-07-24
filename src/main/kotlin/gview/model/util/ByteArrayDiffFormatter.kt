package gview.model.util

import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.lib.Repository
import java.io.ByteArrayOutputStream

/*
    diff出力をByteArrayとして取得可能なDiff Formatter
 */
class ByteArrayDiffFormatter(repository: Repository,
                             private val output: ByteArrayOutputStream): DiffFormatter(output) {

    //出力先ByteArrayインスタンスを内部生成するコンストラクタ
    constructor(repo:Repository) : this(repo, ByteArrayOutputStream())

    //初期化
    init {
        //参照リポジトリ設定
        super.setRepository(repository)
    }

    //Diff出力をByteArrayとして取得
    fun getText(entry: DiffEntry): ByteArray {
        output.reset()
        super.format(entry)
        return output.toByteArray()
    }
}
