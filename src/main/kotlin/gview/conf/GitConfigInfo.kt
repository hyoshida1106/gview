package gview.conf

import gview.GviewApp
import org.eclipse.jgit.lib.UserConfig

object GitConfigInfo {

    val userName get() = userConfig?.committerName ?: ""
    val mailAddr get() = userConfig?.committerEmail ?: ""

    private val config get() = GviewApp.currentRepository.getJgitRepository().config
    private val userConfig get() = config.get(UserConfig.KEY)
}