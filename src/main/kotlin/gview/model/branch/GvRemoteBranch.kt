package gview.model.branch

import org.eclipse.jgit.lib.Ref
import java.lang.ref.WeakReference

/**
 * リモートブランチモデル
 *
 *  @constructor            プライマリコンストラクタ
 *  @param[branchList]      保持されるブランチリスト
 *  @param[ref]             ブランチ情報
 */
class GvRemoteBranch(branchList: GvBranchList, ref: Ref) : GvBranch(branchList, ref) {

    /**
     * リモートブランチ名称
     *
     * パスから取得する。
     */
    override val name: String = branchList.remoteBranchDisplayName(ref.name)

    /**
     * リモートブランチのパス
     */
    override val path: String = ref.name

    /**
     * 関連付けられているローカルブランチの参照
     */
    var localBranch = WeakReference<GvLocalBranch>(null)
}