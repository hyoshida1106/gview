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
        repository.addRefsChangedListener { _ -> update() }
    }

    fun remoteBranchDisplayName(name: String): String {
        return repository.shortenRemoteBranchName(name)
    }
    fun localBranchDisplayName(name: String): String {
        return Repository.shortenRefName(name)
    }

    private fun update() {
        val git = repository.gitCommand

        val remoteBranches = git.branchList()
            .setListMode(ListBranchCommand.ListMode.REMOTE)
            .call()
            .map { GvRemoteBranch(this, it) }

        val localBranches = git.branchList()
            .call()
            .map { GvLocalBranch(this, it) }

        localBranches.forEach { localBranch ->
            val trackingStatus = repository.getTrackingStatus(localBranch.path)
            if(trackingStatus != null) {
                val remoteBranch = remoteBranches.find { it.path == trackingStatus.remoteTrackingBranch }
                if(remoteBranch != null) {
                    localBranch.remoteBranch = WeakReference(remoteBranch)
                    remoteBranch.localBranch = WeakReference(localBranch)
                }
            }
        }

        remoteBranchList.value = remoteBranches
        localBranchList.value  = localBranches
        currentBranch.value = repository.currentBranch
    }

    fun checkoutRemoteBranch(model: GvRemoteBranch) {
        repository.gitCommand
                .checkout()
                .setName(model.name)
                .setStartPoint(model.path)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .setCreateBranch(true)
                .call()
    }

    fun checkoutLocalBranch(model: GvLocalBranch) {
        repository.gitCommand
                .checkout()
                .setName(model.name)
                .call()
    }

    fun removeLocalBranch(model: GvLocalBranch, force: Boolean) {
        repository.gitCommand
                .branchDelete()
                .setBranchNames(model.name)
                .setForce(force)
                .call()
    }

    fun createNewBranchFromHead(newBranch: String, checkout: Boolean) {
        if(checkout) {
            repository.gitCommand
                    .checkout()
                    .setName(newBranch)
                    .setCreateBranch(true)
                    .call()
        } else {
            repository.gitCommand
                    .branchCreate()
                    .setName(newBranch)
                    .call()
        }
    }

    fun createNewBranchFromCommit(newBranch: String, commit: GvCommit, checkout: Boolean) {
        if(checkout) {
            repository.gitCommand
                    .checkout()
                    .setName(newBranch)
                    .setStartPoint(commit.commit)
                    .setCreateBranch(true)
                    .call()
        } else {
            repository.gitCommand
                    .branchCreate()
                    .setName(newBranch)
                    .setStartPoint(commit.commit)
                    .call()
        }
    }

    fun createNewBranchFromOtherBranch(newBranch: String, model: GvLocalBranch, checkout: Boolean) {
        if(checkout) {
            repository.gitCommand
                    .checkout()
                    .setName(newBranch)
                    .setStartPoint(model.path)
                    .setCreateBranch(true)
                    .call()
        } else {
            repository.gitCommand
                    .branchCreate()
                    .setName(newBranch)
                    .setStartPoint(model.path)
                    .call()
        }
    }

	fun mergeCommit(model: GvCommit, message: String) {
        repository.gitCommand
                .merge()
                .include(model.id)
                .setMessage(message)
                .call()
	}
}