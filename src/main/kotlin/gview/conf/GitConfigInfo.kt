package gview.conf

import gview.model.GvRepository

/**
 * Gitコンフィギュレーション情報にアクセスするためのクラス
 */
object GitConfigInfo {
    /**
     * ユーザ名
     */
    val userName get() = userConfig?.committerName ?: ""

    /**
     * ユーザメールアドレス
     */
    val mailAddress get() = userConfig?.committerEmail ?: ""

    /**
     * GITコンフィギュレーション情報を参照するプロパティ
     */
    private val userConfig get() = GvRepository.currentRepository?.userConfig
}