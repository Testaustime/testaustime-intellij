package fi.testaustime.plugin_intellij.services

import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import fi.testaustime.plugin_intellij.TestaustimeBundle.message
import fi.testaustime.plugin_intellij.utils.TestaustimeNotifier


class TestaustimeProjectService(private val project: Project) {
    init {
        projects.add(project);
    }

    fun terminate() {
        projects.remove(project);
    }

    companion object {
        var projects: MutableList<Project> = ArrayList();
        fun broadcast(tokenValid: Boolean) {
            for (project in projects) {
                if (tokenValid) {
                    TestaustimeNotifier.notifyInfo(project, message("projectService.active.title"),
                        message("projectService.active.message", project.name));
                } else {
                    TestaustimeNotifier.notification(NotificationType.ERROR, project, message("projectService.invalid.title"),
                        message("projectService.invalid.message", project.name))
                }
            }
        }
    }
}
