package gview.model

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import java.io.File

object GvDataModel {

    /**
     * リポジトリの新規作成
     *
     * @param directoryPath     作成するディレクトリパス
     * @param isBare            BAREリポジトリ作成
     */
    fun createNew(directoryPath: String, isBare: Boolean = false): Repository {
        return Git.init()
            .setBare(isBare)
            .setDirectory(File(directoryPath))
            .setGitDir(File(directoryPath,".git"))
            .call()
            .repository
    }

    /**
     *  既存リポジトリのオープン
     *
     * @param directoryPath     オープンするディレクトリパス
     */
    fun openExist(directoryPath: String): Repository {
        return Git
            .open(File(directoryPath))
            .repository
    }

    /**
     *  リモートリポジトリのクローン
     *
     * @param directoryPath     クローンするディレクトリパス
     * @param remoteURI         リモートリポジトリ参照
     * @param isBare            BAREリポジトリ作成
     */
    fun clone(directoryPath: String, remoteURI: String, isBare: Boolean = false): Repository {
        return Git.cloneRepository()
            .setURI(remoteURI)
            .setDirectory(File(directoryPath))
            .setBare(isBare)
            .call()
            .repository
    }
}