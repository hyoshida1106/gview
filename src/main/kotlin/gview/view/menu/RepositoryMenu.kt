package gview.view.menu

import gview.model.GvRepository
import gview.resourceBundle
import gview.view.function.RepositoryFunction
import javafx.event.EventHandler
import javafx.scene.control.Menu

class RepositoryMenu: Menu(resourceBundle().getString("RepositoryMenu.Title")) {

    /* フェッチ */
    private val fetchMenu = GvMenuItem(
        text = resourceBundle().getString("RepositoryMenu.Fetch"),
        iconLiteral = "mdi2s-source-branch-refresh"           // NON-NLS
    ) { RepositoryFunction.doFetch(GvRepository.currentRepository) }

    init {
        items.setAll(
            fetchMenu
        )
        onShowing = EventHandler { onShowingMenu() }
    }

    private fun onShowingMenu() {
        fetchMenu.isDisable = !RepositoryFunction.canFetch(GvRepository.currentRepository)
    }
}