package gview.model.branch

import org.eclipse.jgit.lib.Ref

/**
 * ブランチモデル基本抽象クラス
 *
 * @constructor         プライマリコンストラクタ
 * @param[branchList]   所属するブランチリストインスタンス
 * @param[ref]          ブランチ情報
 */
abstract class GvBranch(val branchList: GvBranchList, val ref: Ref) {
    /**
     * ブランチの表示用名称
     */
    abstract val name: String

    /**
     * ブランチのパス
     */
    abstract val path: String

    abstract val localPath: String?
    abstract val remotePath: String?

    val repository = branchList.repository
}