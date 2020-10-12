package gview.model

import gview.model.branch.GviewBranchListModel
import gview.model.commit.GviewCommitListModel
import gview.model.workfile.GviewWorkFilesModel
import gview.model.util.ModelObservable
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import java.io.File

/*
    RepositoryModel
 */
class GviewRepositoryModel: ModelObservable<GviewRepositoryModel>() {

    //JGitリポジトリ
    var jgitRepository: Repository? = null

    //リポジトリインスタンス
    //現状は１つのみ
    companion object {
        val currentRepository = GviewRepositoryModel()
    }

    //インデックス未登録/登録済ファイル情報
    val workFileInfo = GviewWorkFilesModel(this)

    //ブランチ一覧(ローカル/リモート)
    val branches = GviewBranchListModel(this)

    //コミット情報リスト
    val commits = GviewCommitListModel(this)

    //リポジトリ新規作成
    fun createNew(dir : String, bare : Boolean = false) {
        updateRepository(Git
            .init()
            .setBare(bare)
            .setDirectory(File(dir))
            .setGitDir(File(dir,".git"))
            .call()
            .repository)
    }

    //既存リポジトリのオープン
    fun openExist(dir : String) {
        updateRepository(Git
            .open(File(dir))
            .repository)
    }

    //リモートリポジトリのクローン
    fun clone(dir: String, remote: String, bare: Boolean = false) {
        updateRepository(Git
                .cloneRepository()
                .setURI(remote)
                .setDirectory(File(dir))
                .setBare(bare)
                .call()
                .repository)
    }

    //リポジトリインスタンスの更新
    private fun updateRepository(newRepository: Repository?) {
        jgitRepository = newRepository
        workFileInfo.update()
        branches.update()
        fireCallback(this)
    }
}
