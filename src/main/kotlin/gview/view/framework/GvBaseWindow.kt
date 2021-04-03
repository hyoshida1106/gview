package gview.view.framework

import javafx.fxml.FXMLLoader
import javafx.scene.Parent

/**
 * Windowのベースクラス
 *
 * @param Controller    コントローラクラスを指定する
 * @param formPath      FXMLファイルのパスを指定する
 * @param controlClass  コントロールクラスの名称を指定する
 */
open class GvBaseWindow<Controller>(formPath: String, controlClass: String)
        where Controller: GvBaseWindowCtrl {

    /**
     * Windowのベースインスタンス
     */
    val root: Parent

    /**
     * コントローラインスタンス。テンプレートパラメータで型を指示する。
     */
    val controller: Controller      //コントローラ参照

    /**
     * インスタンス初期化
     */
    init {
        //Loaderインスタンスの取得
        val loader = FXMLLoader(javaClass.getResource(formPath))
        //FXMLフォームの読み込み
        root = loader.load()
        //Style Classの追加
        root.styleClass.add(controlClass)
        //コントローラ(FXMLファイルに記載されているもの)を取得して参照を設定
        controller = loader.getController() as Controller
    }
}
