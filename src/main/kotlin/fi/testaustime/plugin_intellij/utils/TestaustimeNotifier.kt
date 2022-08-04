package fi.testaustime.plugin_intellij.utils

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.NotificationType.INFORMATION
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Nullable


object TestaustimeNotifier {
    fun notifyWarning(
        @Nullable project: Project?,
        title: String,
        content: String
    ) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("testaustime.warnings")
            .createNotification(title, content, NotificationType.WARNING)
            .notify(project)
    }

    fun notifyInfo(
        project: Project?,
        title: String,
        content: String
    ) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("testaustime.info")
            .createNotification(title, content, INFORMATION)
            .notify(project)
    }

    fun notification(
        type: NotificationType,
        project: Project?,
        title: String,
        content: String
    ) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(if (type == INFORMATION)
                "testaustime.info" else "testaustime.warnings")
            .createNotification(title, content, type)
            .notify(project)
    }
}