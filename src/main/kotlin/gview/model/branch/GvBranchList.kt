package gview.model.branch

import gview.model.GvRepository
import gview.model.commit.GvCommit
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.events.RepositoryEvent
import org.eclipse.jgit.events.RepositoryListener
import org.eclipse.jgit.lib.Repository
import java.lang.ref.WeakReference

class GvBranchList(val repository: GvRepository){
    val localBranchList  = SimpleObjectProperty<List<GvLocalBranch>>()
    val remoteBranchList = SimpleObjectProperty<List<GvRemoteBranch>>()
    val currentBranch    = SimpleStringProperty("")

    class BranchChangedEvent : RepositoryEvent<BranchChangedListener>() {
        override fun getListenerType(): Class<BranchChangedListener> {
            return BranchChangedListener::class.java
        }
        override fun dispatch(listener: BranchChangedListener?) {
            listener?.onBranchChanged(this)
        }
    }

    fun interface BranchChangedListener : RepositoryListener {
        fun onBranchChanged(event: BranchChangedEvent)
    }

    init {
        update()
        repository.addBranchChangedListener { update() }
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

        localBranches.forEach {
            it.selectedFlagProperty.addListener { _, _, _ ->
                repository.workFileChanged()
                repository.commitChanged()
            }
        }

        remoteBranchList.value = remoteBranches
        localBranchList.value  = localBranches
        currentBranch.value = repository.currentBranch

        repository.workFileChanged()
        repository.commitChanged()
    }

//    fun createNewBranchFromHead(newBranch: String, checkout: Boolean) {
//        if(checkout) {
//            repository.gitCommand
//                    .checkout()
//                    .setName(newBranch)
//                    .setCreateBranch(true)
//                    .call()
//        } else {
//            repository.gitCommand
//                    .branchCreate()
//                    .setName(newBranch)
//                    .call()
//        }
//        repository.branchChanged()
//    }

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
        repository.branchChanged()
    }

//    fun createNewBranchFromOtherBranch(newBranch: String, model: GvLocalBranch, checkout: Boolean) {
//        if(checkout) {
//            repository.gitCommand
//                    .checkout()
//                    .setName(newBranch)
//                    .setStartPoint(model.path)
//                    .setCreateBranch(true)
//                    .call()
//        } else {
//            repository.gitCommand
//                    .branchCreate()
//                    .setName(newBranch)
//                    .setStartPoint(model.path)
//                    .call()
//        }
//        repository.branchChanged()
//    }

	fun mergeCommit(model: GvCommit, message: String) {
        repository.gitCommand
                .merge()
                .include(model.id)
                .setMessage(message)
                .call()
        repository.branchChanged()
	}
}