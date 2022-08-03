package fi.testaustime.plugin_intellij.listeners;

import com.intellij.ide.AppLifecycleListener
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import fi.testaustime.plugin_intellij.services.TestaustimeApplicationService
import fi.testaustime.plugin_intellij.services.TestaustimeProjectService

internal class TestaustimeServiceListener : AppLifecycleListener, ProjectManagerListener {

    override fun appStarted() {
        service<TestaustimeApplicationService>();
    }

    override fun appClosing() {
        service<TestaustimeApplicationService>().terminateService();
    }

    override fun projectOpened(project: Project) {
        project.service<TestaustimeProjectService>()
    }

    override fun projectClosingBeforeSave(project: Project) {
        project.getService(TestaustimeProjectService::class.java).terminate()
    }
}
