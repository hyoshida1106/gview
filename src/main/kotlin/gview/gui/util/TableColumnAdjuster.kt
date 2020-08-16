package gview.gui.util

import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.ScrollBar
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView


class TableColumnAdjuster(private val table: TableView<*>, private val adjustColumn: TableColumn<*, *>) {

    private var verticalScrollBar: ScrollBar? = null

    init {
        table.widthProperty().addListener { _ -> adjustColumnWidth() }
        table.columns.forEach {
            it.widthProperty().addListener { _ -> adjustColumnWidth() }
        }
    }

    /* 縦スクロールバー表示の有無を確認した上で、カラム幅を決定する */
    fun adjustColumnWidth() {
        var width = table.width
        width -= table.snappedLeftInset().toInt()
        width -= table.snappedRightInset().toInt()
        table.columns.forEach {
            if(it != adjustColumn) width -= it.width
        }

        if(verticalScrollBar == null) {
            verticalScrollBar = findVerticalScrollBar(table)
            verticalScrollBar?.widthProperty()?.addListener   { _ -> adjustColumnWidth() }
            verticalScrollBar?.visibleProperty()?.addListener { _ -> adjustColumnWidth() }
        }

        if(verticalScrollBar != null && verticalScrollBar!!.isVisible) {
            width -= verticalScrollBar!!.width
        }

        adjustColumn.prefWidth = width
        adjustColumn.minWidth  = width
        adjustColumn.maxWidth  = width
    }

    private fun findVerticalScrollBar(node: Node): ScrollBar? {
        node.lookupAll(".scroll-bar").forEach {
            var bar:ScrollBar? = it as ScrollBar
            if(bar != null && bar.orientation == Orientation.VERTICAL) {
                return bar
            }
        }
        return null
    }
}