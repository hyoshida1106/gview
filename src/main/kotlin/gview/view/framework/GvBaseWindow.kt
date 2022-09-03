package gview.view.framework

import gview.resourceBundle
import javafx.fxml.FXMLLoader
import javafx.scene.Parent

/**
 * 基本ウィンドウクラス
 *
 * @constructor             プライマリコンストラクタ
 * @param[formPath]         FXMLファイルのパス
 * @param controller        コントローラインスタンス
 */
open class GvBaseWindow<Controller>(formPath: String, val controller: Controller) where Controller : GvBaseWindowCtrl {

    private val cssResource = javaClass.getResource("/Gview.css")   /* NON-NLS */

    /**
     * ルートウィンドウ
     */
    val root: Parent

    /**
     * 初期化
     */
    init {
        val loader = FXMLLoader(javaClass.getResource(formPath), resourceBundle())
        loader.setController(controller)
        root = loader.load()
        root.stylesheets.add(cssResource.toExternalForm())
        root.styleClass.add("GvBaseWindow")
        root.styleClass.add(javaClass.name.substringAfterLast("."))
    }
}
