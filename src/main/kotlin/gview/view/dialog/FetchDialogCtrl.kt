package gview.view.dialog

import gview.view.framework.GvCustomDialogCtrl
import gview.view.util.GvColumnAdjuster
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import org.eclipse.jgit.transport.RemoteConfig

class FetchDialogCtrl: GvCustomDialogCtrl() {

    @FXML lateinit var remoteList: TableView<RowData>
    @FXML lateinit var nameColumn: TableColumn<RowData, String>
    @FXML lateinit var urlColumn: TableColumn<RowData, String>
    @FXML lateinit var pruneCheck: CheckBox

    val remote: String get() = remoteList.selectionModel.selectedItem.remote.name
    val prune: Boolean get() = pruneCheck.isSelected

    class RowData(val remote: RemoteConfig) {
        val remoteName: String = remote.name
        val urlPath: String = remote.urIs[0].path
    }

    var remoteConfigList = SimpleObjectProperty<List<RemoteConfig>>()

    private lateinit var remoteListAdjuster: GvColumnAdjuster

    override fun initialize() {
        urlColumn.cellValueFactory = PropertyValueFactory("urlPath")
        nameColumn.cellValueFactory = PropertyValueFactory("remoteName")
        remoteListAdjuster = GvColumnAdjuster(remoteList, urlColumn)
        remoteConfigList.addListener { _, _, it -> updateRemoteConfigList(it) }
        remoteList.requestFocus()
    }

    private fun updateRemoteConfigList(remoteConfigList: List<RemoteConfig>) {
        remoteList.items.setAll(remoteConfigList.map { RowData(it) })
        remoteList.selectionModel.selectionMode = SelectionMode.SINGLE
        remoteList.selectionModel.selectFirst()
    }
}

