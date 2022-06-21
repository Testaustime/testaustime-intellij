package fi.testaustime.plugin_intellij.utils

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Nullable


object TestausTimeNotifier {
    fun notifyWarning(
        @Nullable project: Project?,
        content: String?
    ) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("TestausTime Notifications")
            .createNotification("Testaustime", content!!, NotificationType.WARNING)
            .notify(project)
    }
}