package gview.conf

import gview.model.GvRepository
import org.eclipse.jgit.lib.UserConfig

object GitConfigInfo {

    val userName get() = userConfig?.committerName ?: ""
    val mailAddr get() = userConfig?.committerEmail ?: ""

    private val config get() = GvRepository.currentRepository?.jgitRepository?.config
    private val userConfig get() = config?.get(UserConfig.KEY)
}