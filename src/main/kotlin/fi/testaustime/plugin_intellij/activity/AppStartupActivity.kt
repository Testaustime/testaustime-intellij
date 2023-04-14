package fi.testaustime.plugin_intellij.activity

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import fi.testaustime.plugin_intellij.services.TestaustimeApplicationService
import java.util.concurrent.atomic.AtomicBoolean

internal class AppStartupActivity : StartupActivity.DumbAware {

    private var initialized: AtomicBoolean = AtomicBoolean(false);

    override fun runActivity(project: Project) {
        if (initialized.compareAndSet(false, true)) {
            service<TestaustimeApplicationService>()
        }
    }
}