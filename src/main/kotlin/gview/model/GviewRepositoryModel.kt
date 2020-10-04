package gview.model

import gview.model.branch.GviewBranchListModel
import gview.model.commit.GviewHeadFilesModel
import gview.model.util.ModelObservable
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import java.io.File

/*
    RepositoryModel
 */
class GviewRepositoryModel: ModelObservable<GviewRepositoryModel>() {

    //リポジトリインスタンス
    //現状は１つのみ
    companion object {
        val currentRepository = GviewRepositoryModel()
    }

    //インデックス未登録/登録済ファイル情報
    val headerFiles = GviewHeadFilesModel(this)

    //ブランチ一覧(ローカル/リモート)
    val branches = GviewBranchListModel(this)

    //JGitリポジトリ
    var jgitRepository: Repository? = null

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
        headerFiles.update()
        branches.update()
        fireCallback(this)
    }
}
