package gview.model

import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.*
import java.io.File

/*
    RepositoryModel
 */
class GviewRepositoryModel {

    //リポジトリインスタンス
    //現状は１つのみ
    companion object {
        val currentRepository = GviewRepositoryModel()
    }

    //インデックス未登録/登録済ファイル情報
    val headerFiles = GviewHeadFilesModel()

    //ブランチ一覧(ローカル/リモート)
    val branches = GviewBranchListModel()

    //JGitリポジトリ
    var jgitRepository: Repository? = null

    //HEADのObject ID
    var headerId: ObjectId? = null

    //リポジトリのローカスパス
    val localRepositoryPathProperty = SimpleStringProperty()

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
        Platform.runLater {
            jgitRepository = newRepository
            refresh()
        }
    }

    //表示更新
    fun refresh() {
        val repository = jgitRepository
        if(repository != null) {
            localRepositoryPathProperty.value = repository.directory.absolutePath
            headerId = repository.resolve(Constants.HEAD)
        } else {
            localRepositoryPathProperty.value = ""
            headerId = null
        }
        branches.update(repository)
        headerFiles.update(repository, headerId)
    }
}
