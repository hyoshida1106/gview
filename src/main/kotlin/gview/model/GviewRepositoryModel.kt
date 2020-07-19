package gview.model

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import java.io.File

/*
    RepositoryModel
 */
class GviewRepositoryModel {

    //JGitリポジトリ
    val repositoryProperty: ObjectProperty<Repository>
    val repository: Repository? get() { return repositoryProperty.value }

    //ブランチ一覧(ローカル/リモート)
    val branchList = GviewBranchListModel()

    //現在のブランチ名称
    val branchName: String? get() { return repository?.branch }
    val fullBranch: String? get() { return repository?.fullBranch }

    //リポジトリのローカスパス
    val localPathProperty: StringProperty
    val localPath: String get() { return localPathProperty.value }

    //初期化
    init {
        repositoryProperty = SimpleObjectProperty<Repository>(null)
        localPathProperty  = SimpleStringProperty("")

        repositoryProperty.addListener { _, _, newRepository ->
            //ローカスパスの設定
            localPathProperty.value = newRepository.directory.absolutePath
            //
            branchList.update(newRepository)
        }
    }

    //リポジトリ新規作成
    fun createNew(dir : String, bare : Boolean = false) {
        repositoryProperty.value = Git
            .init()
            .setBare(bare)
            .setDirectory(File(dir))
            .setGitDir(File(dir,".git"))
            .call()
            .repository
    }

    //既存リポジトリのオープン
    fun openExist(dir : String) {
        repositoryProperty.value = Git
            .open(File(dir))
            .repository
    }

    //リモートリポジトリのクローン
    fun clone(dir : String, remote : String, bare : Boolean = false) {
        repositoryProperty.value = Git
            .cloneRepository()
            .setURI(remote)
            .setDirectory(File(dir))
            .setBare(bare)
            .call()
            .repository
    }
}