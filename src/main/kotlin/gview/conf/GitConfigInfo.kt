package gview.conf

import gview.model.GviewRepositoryModel
import org.eclipse.jgit.lib.StoredConfig

object GitConfigInfo {

    val userName: String
        get() = config?.getString("user", null, "name") ?: ""

    val mailAddr: String
        get() = config?.getString("user", null, "email") ?: ""

    private val config: StoredConfig?
        get() = GviewRepositoryModel.currentRepository.jgitRepository?.config
}