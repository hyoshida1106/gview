package gview.gui.util

import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.ScrollBar

fun verticalScrollBar(node: Node): ScrollBar? {
    node.lookupAll(".scroll-bar").forEach {
        var bar:ScrollBar? = it as ScrollBar
        if(bar != null && bar.orientation == Orientation.VERTICAL) {
            return bar
        }
    }
    return null
}