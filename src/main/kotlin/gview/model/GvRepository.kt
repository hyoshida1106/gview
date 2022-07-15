package gview.model

import gview.model.branch.GvBranchList
import gview.model.commit.GvCommitList
import gview.model.workfile.GvWorkFilesModel
import javafx.beans.property.SimpleObjectProperty
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import java.io.File

/**
 * リポジトリモデル
 *
 * @constructor コンストラクタ
 * @param[jgitRepository] JGitのリポジトリインスタンス
 */
class GvRepository private constructor(val jgitRepository: Repository) {

    /**
     * 作業ファイル情報
     */
    val workFiles = GvWorkFilesModel(this)

    /**
     * ブランチ情報リスト(リモート/ローカルブランチ)
     */
    val branches = GvBranchList(this)

    /**
     * コミット情報リスト
     */
    val commits = GvCommitList(this)

//    init {
//        jgitRepository.listenerList.addWorkingTreeModifiedListener {
//            println("work tree changed")
//        }
//        jgitRepository.listenerList.addIndexChangedListener {_ ->
//            println("index changed")
//        }
//        jgitRepository.listenerList.addRefsChangedListener { _ ->
//            println("Refs changed")
//        }
//        jgitRepository.listenerList.addConfigChangedListener { _ ->
//            println("Config changed")
//        }
//    }

    /**
     * シングルトン管理のための Companion Object
     */
    companion object {
        /**
         * 現在有効なリポジトリモデルを保持する
         */
        val currentRepositoryProperty = SimpleObjectProperty<GvRepository>()

        /**
         * 現在有効なリポジトリインスタンスを参照するためのプロパティ
         */
        val currentRepository: GvRepository? get() = currentRepositoryProperty.value

        /**
         * リポジトリを新規作成し、カレントリポジトリとする
         *
         * @param[directoryPath]    リポジトリを作成するパス
         * @param[isBare]           Bareリポジトリを生成する場合、trueを指定する
         */
        fun init(directoryPath: String, isBare: Boolean = false) {
            currentRepositoryProperty.set(
                GvRepository(
                    Git.init()
                        .setBare(isBare)
                        .setDirectory(File(directoryPath))
                        .setGitDir(File(directoryPath, ".git"))
                        .call()
                        .repository
                )
            )
        }

        /**
         * ローカルリポジトリをオープンし、カレントリポジトリとする
         *
         * @param[directoryPath]    オープンするディレクトリのパス
         */
        fun open(directoryPath: String) {
            currentRepositoryProperty.set(
                GvRepository(
                    Git.open(File(directoryPath))
                        .repository
                )
            )
        }

        /**
         * リモートリポジトリのクローンを生成し、カレントリポジトリとする
         *
         * @param[directoryPath]    ローカルリポジトリを生成するディレクトリのパス
         * @param[remoteUrl]        クローンするリモートリポジトリのURL
         * @param[isBare]           生成するリポジトリがBareの場合、trueを指定する
         */
        fun clone(directoryPath: String, remoteUrl: String, isBare: Boolean = false) {
            currentRepositoryProperty.set(
                GvRepository(
                    Git.cloneRepository()
                        .setURI(remoteUrl)
                        .setDirectory(File(directoryPath))
                        .setBare(isBare)
                        .call()
                        .repository
                )
            )
        }
    }
}
