package gview.model

import gview.model.branch.GvBranchList
import gview.model.commit.GvCommitListModel
import gview.model.workfile.GvWorkFilesModel
import javafx.beans.property.SimpleObjectProperty
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import java.io.File

/*
    RepositoryModel
 */
class GvRepository private constructor(val jgitRepository: Repository) {

    val workFiles = GvWorkFilesModel(this)
    val branches = GvBranchList(this)
    val commits = GvCommitListModel(this)

    companion object {
        val currentRepositoryProperty = SimpleObjectProperty<GvRepository>()
        val currentRepository: GvRepository? get() = currentRepositoryProperty.value

        //リポジトリ新規作成
        fun init(directoryPath: String, isBare: Boolean = false) {
            currentRepositoryProperty.set(GvRepository(
                Git.init()
                    .setBare(isBare)
                    .setDirectory(File(directoryPath))
                    .setGitDir(File(directoryPath, ".git"))
                    .call()
                    .repository
            ))
        }

        //既存リポジトリのオープン
        fun open(directoryPath: String) {
            currentRepositoryProperty.set(GvRepository(
                Git.open(File(directoryPath))
                    .repository
            ))
        }

        //リモートリポジトリのクローン
        fun clone(directoryPath: String, remoteUrl: String, isBare: Boolean = false) {
            currentRepositoryProperty.set(GvRepository(
                Git.cloneRepository()
                    .setURI(remoteUrl)
                    .setDirectory(File(directoryPath))
                    .setBare(isBare)
                    .call()
                    .repository
            ))
        }
    }
}
