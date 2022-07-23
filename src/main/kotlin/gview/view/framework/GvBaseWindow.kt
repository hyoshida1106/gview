package gview.view.framework

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import org.jetbrains.annotations.NonNls

/**
 * 基本ウィンドウクラス
 *
 * @constructor             プライマリコンストラクタ
 * @param[formPath]         FXMLファイルのパス
 * @param[style]            スタイルクラス名称
 */
@NonNls
open class GvBaseWindow<Controller>(formPath: String, style: String) where Controller : GvBaseWindowCtrl {
    /**
     * ルートウィンドウ
     */
    val root: Parent

    /**
     * コントローラインスタンス参照
     */
    val controller: Controller

    /**
     * 初期化
     */
    init {
        //ウィンドウクラスをロードし、styleを登録する
        val loader = FXMLLoader(javaClass.getResource(formPath))
        root = loader.load()
        root.styleClass.add(style)
        //コントローラインスタンスを取得する
        controller = loader.getController() as Controller
    }
}
