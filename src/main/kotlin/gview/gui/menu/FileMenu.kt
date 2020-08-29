package gview.gui.menu

import gview.gui.framework.GviewBaseMenu
import gview.gui.framework.GviewCommonDialog
import gview.gui.main.MainWindow
import gview.model.GviewRepositoryModel
import javafx.fxml.FXML
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import kotlin.system.exitProcess

object FileMenu: GviewBaseMenu<FileMenuCtrl>("/menu/FileMenu.fxml")

class FileMenuCtrl {

    //メニュー関数
    @FXML private fun onMenuOpenRepository() = openRepository()
    @FXML private fun onMenuCreateNewRepository() = createRepository()
    @FXML private fun onMenuQuit() = checkQuit()

    //"File"メニュー表示
    @FXML private fun onShowingMenu() {
    }

    //既存リポジトリを開く
    private fun openRepository() {
        val chooser = DirectoryChooser()
        chooser.title = "リポジトリを開く"
        val dir = chooser.showDialog(MainWindow.root.scene.window as? Stage?)
        if(dir != null) {
            try {
                GviewRepositoryModel.currentRepository.openExist(dir.path)
            } catch(e: Exception) {
                GviewCommonDialog.errorDialog(e)
            }
        }
    }

    //リポジトリ新規作成
    private fun createRepository() {
        val chooser = DirectoryChooser()
        chooser.title = "リポジトリ新規作成"
        val dir = chooser.showDialog(MainWindow.root.scene.window as? Stage?)
        if(dir != null) {
            try {
                GviewRepositoryModel.currentRepository.createNew(dir.absolutePath)
            } catch(e: Exception) {
                GviewCommonDialog.errorDialog(e)
            }
        }
    }

    //終了確認
    private fun checkQuit() {
        if(GviewCommonDialog.confirmationDialog("アプリケーションを終了しますか？")) {
            exitProcess(0)
        }
    }

}
