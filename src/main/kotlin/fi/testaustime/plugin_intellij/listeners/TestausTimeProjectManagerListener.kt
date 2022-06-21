package fi.testaustime.plugin_intellij.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import fi.testaustime.plugin_intellij.services.TestausTimeProjectService

internal class TestausTimeProjectManagerListener : ProjectManagerListener {

    override fun projectOpened(project: Project) {
        project.service<TestausTimeProjectService>()
    }

    override fun projectClosed(project: Project) {
        project.getService(TestausTimeProjectService::class.java).terminateService()
    }
}
