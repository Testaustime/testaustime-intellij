package fi.testaustime.plugin_intellij.services

import com.intellij.ide.DataManager
import com.intellij.notification.NotificationType.ERROR
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiUtilBase
import com.intellij.util.concurrency.AppExecutorUtil
import fi.testaustime.plugin_intellij.TestaustimeBundle
import fi.testaustime.plugin_intellij.TestaustimeBundle.message
import fi.testaustime.plugin_intellij.configuration.SettingsState
import fi.testaustime.plugin_intellij.network.TestaustimeAPIClient
import fi.testaustime.plugin_intellij.network.models.ActivityPostPayload
import fi.testaustime.plugin_intellij.utils.TestaustimeNotifier
import java.net.InetAddress
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class TestaustimeApplicationService {

    init {
        Logger.getInstance("#Testaustime").debug(message("applicationService.started"))
        startScheduledPinger();
    }

    private var scheduledPingTask: ScheduledFuture<*>? = null;

    // Track last project to know when to flush sessions
    private var lastProject: Project? = null;

    // API token invalidation state, used for notification tracking
    private var wasInvalid: Boolean = true;
    private var isInvalid: Boolean = false;

    // Past connection exception state, used for de-duping connection exception notifications
    private var didFail: Boolean = false;

    fun terminateService() {
        flushActivity();
        scheduledPingTask?.cancel(true)
        Logger.getInstance("#Testaustime").debug(message("applicationService.terminated"))
    }

    private fun flushActivity() {
        val settings = SettingsState.instance;
        val client = TestaustimeAPIClient(settings.apiBaseUrl, settings.authToken);
        client.flushActivity().join();
    }

    private fun startScheduledPinger() {
        Logger.getInstance("#Testaustime").debug("Call for schedule ping registration")
        if (scheduledPingTask != null) return
        scheduledPingTask = AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay({
            pingNow()
        }, 0, 30, TimeUnit.SECONDS)
        Logger.getInstance("#Testaustime").debug(
            message("applicationService.pinger.registered")
        )
    }

    fun pingNow() {
        val settings = SettingsState.instance;

        try {
            val client = TestaustimeAPIClient(settings.apiBaseUrl, settings.authToken);

            val hostname = InetAddress.getLocalHost().hostName
            val appName = ApplicationInfo.getInstance().fullApplicationName
            val dataContext = DataManager.getInstance().dataContextFromFocusAsync
            dataContext.onSuccess { ctx ->
                if (isInvalid) {
                    wasInvalid = true;
                    return@onSuccess;
                }

                ctx.getData(PlatformDataKeys.PROJECT)?.let { project ->
                    // Consider switching projects to be a new session
                    if (lastProject?.equals(project) == false) {
                        flushActivity()
                        lastProject = project
                    }

                    val editor: Editor? = ctx.getData(PlatformDataKeys.EDITOR)
                    val type: String?

                    val future: CompletableFuture<HttpResponse<String>>
                    if (project.isOpen && editor != null && settings.authToken.isNotBlank()) {
                        type = PsiUtilBase.getPsiFileInEditor(editor, project)?.fileType?.displayName
                        future = client.activityLog(
                            ActivityPostPayload(
                                programmingLanguage = type,
                                projectName = project.name,
                                IDEName = appName,
                                host = hostname
                            )
                        );
                    } else {
                        future = client.me();
                    }

                    future.thenAccept { resp ->
                        if (resp.statusCode() == 401) {
                            TestaustimeProjectService.broadcast(false)
                            isInvalid = true
                            wasInvalid = true
                            return@thenAccept
                        } else if (wasInvalid || didFail) {
                            TestaustimeProjectService.broadcast(true)
                            wasInvalid = false
                            didFail = false;
                        }

                    }.exceptionally {
                        if (!didFail) {
                            TestaustimeNotifier.notification(
                                ERROR, null,
                                message("applicationService.heartbeat.failed"), it.localizedMessage
                            )
                            didFail = true;
                        }
                        return@exceptionally null;
                    }
                }
            }
            dataContext.onError { err ->
                Logger.getInstance("#Testaustime").error(err)
            }
        } catch (e: Exception) {
            Logger.getInstance("#Testaustime").error(e)
        }
    }
}
