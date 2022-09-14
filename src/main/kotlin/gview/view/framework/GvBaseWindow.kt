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

    /**
     * 継承クラスの名称
     */
    private val className: String = javaClass.name.substringAfterLast(".")

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
        root.stylesheets.add(javaClass.getResource("/Gview.css").toExternalForm())    //NON-NLS
        val localCSS = javaClass.getResource("/css/${className}.css")                 //NON-NLS
        if(localCSS != null) {
            root.stylesheets.add(localCSS.toExternalForm())
        }
        root.styleClass.add("GvBaseWindow")                                           //NON-NLS
        root.styleClass.add(className)
    }
}
