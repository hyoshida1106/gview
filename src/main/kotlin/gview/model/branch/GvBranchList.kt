package gview.model.branch

import gview.model.GvRepository
import gview.model.commit.GvCommit
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.BranchTrackingStatus
import org.eclipse.jgit.lib.Repository
import java.lang.ref.WeakReference


class GvBranchList(private val repository: GvRepository){
    val localBranchList  = SimpleObjectProperty<List<GvLocalBranch>>()
    val remoteBranchList = SimpleObjectProperty<List<GvRemoteBranch>>()
    val currentBranch    = SimpleStringProperty("")

    init {
        update()
    }

    fun remoteBranchDisplayName(name: String): String {
        return repository.jgitRepository.shortenRemoteBranchName(name)
    }
    fun localBranchDisplayName(name: String): String {
        return Repository.shortenRefName(name)
    }

    private fun update() {
        val git = Git(repository.jgitRepository)

        val remoteBranches = mutableListOf<GvRemoteBranch>()
        val remoteBranchMap = mutableMapOf<String, GvRemoteBranch>()
        git.branchList()
            .setListMode(ListBranchCommand.ListMode.REMOTE)
            .call()
            .forEach {
                val remoteBranch = GvRemoteBranch(this, it)
                remoteBranches.add(remoteBranch)
                remoteBranchMap[remoteBranch.path] = remoteBranch
            }
        remoteBranchList.value = remoteBranches

        val localBranches = mutableListOf<GvLocalBranch>()
        git.branchList()
            .call()
            .forEach {
                val localBranch = GvLocalBranch(this, it)
                localBranches.add(localBranch)
                val trackingStatus = BranchTrackingStatus.of(repository.jgitRepository, localBranch.path)
                if (trackingStatus != null) {
                    val remoteBranch = remoteBranchMap[trackingStatus.remoteTrackingBranch]
                    if (remoteBranch != null) {
                        localBranch.remoteBranch = WeakReference(remoteBranch)
                        remoteBranch.localBranch = WeakReference(localBranch)
                    }
                }
            }
        localBranchList.value = localBranches
        currentBranch.value = repository.jgitRepository.branch
    }

    fun checkoutRemoteBranch(model: GvRemoteBranch) {
        Git(repository.jgitRepository)
                .checkout()
                .setName(model.name)
                .setStartPoint(model.path)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .setCreateBranch(true)
                .call()
        Platform.runLater { update() }
    }

    fun checkoutLocalBranch(model: GvLocalBranch) {
        Git(repository.jgitRepository)
                .checkout()
                .setName(model.name)
                .call()
        Platform.runLater { update() }
    }

    fun removeLocalBranch(model: GvLocalBranch, force: Boolean) {
        Git(repository.jgitRepository)
                .branchDelete()
                .setBranchNames(model.name)
                .setForce(force)
                .call()
        Platform.runLater { update() }
    }

    fun createNewBranchFromHead(newBranch: String, checkout: Boolean) {
        if(checkout) {
            Git(repository.jgitRepository)
                    .checkout()
                    .setName(newBranch)
                    .setCreateBranch(true)
                    .call()
        } else {
            Git(repository.jgitRepository)
                    .branchCreate()
                    .setName(newBranch)
                    .call()
        }
        Platform.runLater { update() }
    }

    fun createNewBranchFromCommit(newBranch: String, commit: GvCommit, checkout: Boolean) {
        if(checkout) {
            Git(repository.jgitRepository)
                    .checkout()
                    .setName(newBranch)
                    .setStartPoint(commit.revCommit)
                    .setCreateBranch(true)
                    .call()
        } else {
            Git(repository.jgitRepository)
                    .branchCreate()
                    .setName(newBranch)
                    .setStartPoint(commit.revCommit)
                    .call()
        }
        Platform.runLater { update() }
    }

    fun createNewBranchFromOtherBranch(newBranch: String, model: GvLocalBranch, checkout: Boolean) {
        if(checkout) {
            Git(repository.jgitRepository)
                    .checkout()
                    .setName(newBranch)
                    .setStartPoint(model.path)
                    .setCreateBranch(true)
                    .call()
        } else {
            Git(repository.jgitRepository)
                    .branchCreate()
                    .setName(newBranch)
                    .setStartPoint(model.path)
                    .call()
        }
        Platform.runLater { update() }
    }

	fun mergeCommit(model: GvCommit, message: String) {
        Git(repository.jgitRepository)
                .merge()
                .include(model.id)
                .setMessage(message)
                .call()
        Platform.runLater { update() }
	}
}