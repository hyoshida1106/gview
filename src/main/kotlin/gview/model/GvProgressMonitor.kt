package gview.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import org.eclipse.jgit.lib.ProgressMonitor


class GvProgressMonitor: ProgressMonitor {

    val scaleProperty = SimpleIntegerProperty(0)
    val valueProperty = SimpleIntegerProperty(0)
    val titleProperty = SimpleStringProperty("")

    val title get() = titleProperty.value ?: ""
    val scale get() = scaleProperty.value ?: 0
    val value get() = valueProperty.value ?: 0
    var cancel = false

    override fun start(totalTasks: Int) {
    }

    override fun beginTask(title: String?, totalWork: Int) {
        titleProperty.value = title
        scaleProperty.value = totalWork
        valueProperty.value = 0
    }

    override fun update(completed: Int) {
        valueProperty.value += completed
    }

    override fun endTask() {
    }

    override fun isCancelled(): Boolean {
        return cancel
    }
}