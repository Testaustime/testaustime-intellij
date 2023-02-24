package fi.testaustime.plugin_intellij.services

import com.intellij.notification.NotificationType.ERROR
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.AppExecutorUtil
import fi.testaustime.plugin_intellij.TestaustimeBundle.message
import fi.testaustime.plugin_intellij.configuration.SettingsState
import fi.testaustime.plugin_intellij.network.TestaustimeAPIClient
import fi.testaustime.plugin_intellij.network.models.ActivityPostPayload
import fi.testaustime.plugin_intellij.utils.ContextInformation
import fi.testaustime.plugin_intellij.utils.TestaustimeNotifier
import java.time.Duration
import java.time.Duration.ofSeconds
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit.MILLISECONDS

class TestaustimeApplicationService {
    companion object {
        private val HEARTBEAT_PERIOD: Duration = ofSeconds(30);
    }

    init {
        Logger.getInstance("#Testaustime").debug(message("applicationService.started"))
        startScheduledPinger()
    }

    private var scheduledPingTask: ScheduledFuture<*>? = null

    // Track last project to know when to flush sessions
    private var lastProject: Project? = null


    // Past connection exception state, used for de-duping connection exception notifications
    private var didFail: Boolean = false

    fun terminateService() {
        flushActivity()
        scheduledPingTask?.cancel(true)
        Logger.getInstance("#Testaustime").debug(message("applicationService.terminated"))
    }

    private fun flushActivity() {
        val settings = SettingsState.instance
        val client = TestaustimeAPIClient(settings.apiBaseUrl, settings.authToken)
        client.flushActivity().join()
    }

    private fun startScheduledPinger() {
        Logger.getInstance("#Testaustime").debug("Call for schedule ping registration")
        if (scheduledPingTask != null) return
        scheduledPingTask = AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay({
            pingNow()
        }, 0, HEARTBEAT_PERIOD.toMillis(), MILLISECONDS)
        Logger.getInstance("#Testaustime").debug(
            message("applicationService.pinger.registered")
        )
    }

    fun pingNow() {
        val settings = SettingsState.instance

        try {
            val client = TestaustimeAPIClient(settings.apiBaseUrl, settings.authToken)

            ApplicationManager.getApplication().invokeLater({
                val ctx = ContextInformation.getDataContext() ?: return@invokeLater;
                val project = ctx.getData(CommonDataKeys.PROJECT)
                project ?: return@invokeLater

                // Consider switching projects to be a new session
                if (lastProject?.equals(project) == false) {
                    flushActivity()
                    lastProject = project
                }

                val editor: Editor? = ctx.getData(PlatformDataKeys.EDITOR)
                val service = project.getService(TestaustimeProjectService::class.java)
                val future = if (fun(): Boolean {
                    if (!project.isOpen) return false
                    if (settings.authToken.isBlank()) return false
                    if (!service.isActive()) return false;
                    editor ?: return false

                    return true
                }.invoke()) {
                    client.activityLog(ActivityPostPayload.fromProject(project))
                } else client.me()

                future.thenAccept { resp ->
                    if (resp.statusCode() == 401) {
                        TestaustimeProjectService.broadcast(false)
                        return@thenAccept
                    } else if (didFail) {
                        TestaustimeProjectService.broadcast(true)
                        didFail = false
                    }
                }.exceptionally {
                    if (!didFail) {
                        TestaustimeNotifier.notification(
                            ERROR, null,
                            message("applicationService.heartbeat.failed"), it.localizedMessage
                        )

                        didFail = true
                    }

                    return@exceptionally null
                }
            }, ModalityState.any())
        } catch (e: Throwable) {
            TestaustimeNotifier.notification(
                ERROR, null,
                message("applicationService.heartbeat.failed"), e.localizedMessage
            )

            Logger.getInstance("#Testaustime").error(e)
        }
    }
}
