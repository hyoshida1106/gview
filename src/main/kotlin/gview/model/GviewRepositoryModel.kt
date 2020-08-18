package gview.model

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import java.io.File

/*
    RepositoryModel
 */
class GviewRepositoryModel {

    //JGitリポジトリ
    private val repositoryProperty = SimpleObjectProperty<Repository>()
    private val repository: Repository? get() { return repositoryProperty.value }

    //ブランチ一覧(ローカル/リモート)
    val branchList: GviewBranchListModel

    //現在のブランチ名称
    val branchName: String? get() { return repository?.branch }

    //リポジトリのローカスパス
    val localPathProperty = SimpleStringProperty()
    val localPath: String get() { return localPathProperty.value }

    //インデックス未登録/登録済ファイル情報
    val headerFiles: GviewHeadFilesModel

    //HEADのObject ID
    private val headIdProperty = SimpleObjectProperty<ObjectId?>()

    //初期化
    init {
        branchList  = GviewBranchListModel(repositoryProperty)
        headerFiles = GviewHeadFilesModel(repositoryProperty, headIdProperty)

        repositoryProperty.addListener { _, _, newRepository ->
            //ローカスパスの設定
            localPathProperty.value = newRepository?.directory?.absolutePath
            //HEAD IDを更新
            headIdProperty.value = newRepository?.resolve(Constants.HEAD)
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
