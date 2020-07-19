package gview.gui.framework

import javafx.fxml.FXMLLoader
import javafx.scene.Parent

open class BaseView<Controller>(form: String, className: String) {
    val root: Parent                //親インスタンス参照
    val controller: Controller      //コントローラ参照

    init {
        //FXMLフォームの読み込み
        val loader = FXMLLoader(javaClass.getResource(form))
        root = loader.load()
        //Style Classの追加
        root.styleClass.add(className)
        //コントローラ(FXMLファイルに記載されているもの)を取得して参照を設定
        controller = loader.getController() as Controller
    }
}
