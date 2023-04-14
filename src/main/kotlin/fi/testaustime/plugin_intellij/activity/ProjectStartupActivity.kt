package fi.testaustime.plugin_intellij.activity

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import fi.testaustime.plugin_intellij.services.TestaustimeProjectService

internal class ProjectStartupActivity : StartupActivity.DumbAware {
    override fun runActivity(project: Project) {
        project.service<TestaustimeProjectService>()
    }
}