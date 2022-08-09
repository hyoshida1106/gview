package gview.view.menu

import gview.model.GvRepository
import gview.resourceBundle
import gview.view.function.RepositoryFunction
import javafx.event.EventHandler
import javafx.scene.control.Menu
import org.jetbrains.annotations.NonNls

class RepositoryMenu: Menu(resourceBundle().getString("RepositoryMenu.Title")) {

    @NonNls
    private val fetchMenu = GvMenuItem(
        text = resourceBundle().getString("RepositoryMenu.Fetch"),
        iconLiteral = "mdi2s-source-branch-refresh"
    ) {
        RepositoryFunction.doFetch(GvRepository.currentRepository)
    }

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