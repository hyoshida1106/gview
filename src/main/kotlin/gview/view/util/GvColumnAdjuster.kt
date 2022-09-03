package gview.view.util

import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.ScrollBar
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView

/**
 * Tableのサイズ変更時に、指定されたカラムの幅を調整する
 *
 * @param table         監視対象テーブル
 * @param adjustColumn  調整対象カラム
 */
class GvColumnAdjuster(private val table: TableView<*>, private val adjustColumn: TableColumn<*, *>) {

    /**
     * スクロールバーが表示されている場合、そのハンドルを保持する
     */
    private var verticalScrollBar: ScrollBar? = null

    init {
        //テーブルサイズ変更リスナ
        table.widthProperty().addListener { _ -> adjustColumnWidth() }
        //テーブルのカラム幅変更リスナ
        table.columns.forEach {
            it.widthProperty().addListener { _ -> adjustColumnWidth() }
        }
    }

    /**
     * 縦スクロールバー表示の有無を確認した上で、カラム幅を決定する
     */
    fun adjustColumnWidth() {
        //テーブル幅に収まるカラム幅を計算する
        var width = table.width
        width -= table.snappedLeftInset().toInt()
        width -= table.snappedRightInset().toInt()
        table.columns.forEach {
            if(it != adjustColumn) width -= it.width
        }
        //スクロールバーが未検出であれば、探しておく
        if(verticalScrollBar == null) {
            verticalScrollBar = findVerticalScrollBar(table)
            //幅と可視/不可視が変更された場合には、カラム幅を再計算する
            verticalScrollBar?.widthProperty()?.addListener   { _ -> adjustColumnWidth() }
            verticalScrollBar?.visibleProperty()?.addListener { _ -> adjustColumnWidth() }
        }
        //スクロールバーが表示されていれば、その幅を引いておく
        if(verticalScrollBar != null && verticalScrollBar!!.isVisible) {
            width -= verticalScrollBar!!.width
        }
        //カラム幅を設定する
        if(width >= 0) {
            adjustColumn.prefWidth = width
            adjustColumn.minWidth = width
            adjustColumn.maxWidth = width
        }
    }

    /**
     * 縦スクロールバーを検索する
     *
     * @param node      検索対象node
     * @return          縦スクロールバーのハンドルまたはnull
     */
    private fun findVerticalScrollBar(node: Node): ScrollBar? {
        return node.lookupAll(".scroll-bar")    //NON-NLS
            .find { (it as ScrollBar).orientation == Orientation.VERTICAL } as ScrollBar?
    }
}