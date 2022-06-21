package fi.testaustime.plugin_intellij.services

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiUtilBase
import com.intellij.util.concurrency.AppExecutorUtil
import fi.testaustime.plugin_intellij.TestausTimeBundle
import fi.testaustime.plugin_intellij.configuration.TestausTimeSettingsState
import fi.testaustime.plugin_intellij.network.TestausTimeApiClient
import fi.testaustime.plugin_intellij.network.models.ActivityPostPayload
import fi.testaustime.plugin_intellij.utils.TestausTimeNotifier
import okhttp3.Callback
import java.net.InetAddress
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


class TestausTimeProjectService(private val project: Project) {

    private var scheduledPingTask: ScheduledFuture<*>? = null;

    init {
        println(TestausTimeBundle.message("projectService", project.name))
        startScheduledPinger();
    }

    fun terminateService() {
        println("Service terminated")
        scheduledPingTask?.cancel(true)
    }

    private fun startScheduledPinger() {
        println("Call for schedule ping registration")
        if (scheduledPingTask != null) return
        val settings = TestausTimeSettingsState.instance;
        scheduledPingTask = AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay({
            try {
                val hostname = InetAddress.getLocalHost().hostName
                val appName = ApplicationInfo.getInstance().fullApplicationName
                val dataContext = DataManager.getInstance().dataContextFromFocusAsync
                dataContext.onSuccess { ctx ->
                    val project: Project? = ctx.getData(PlatformDataKeys.PROJECT)
                    if (project?.isOpen == true) {
                        if (settings.authToken.isBlank()) {
                            // Notify
                            TestausTimeNotifier.notifyWarning(project, "Authentication token is not configured!\nPlease configure in plugin settings")
                            scheduledPingTask?.cancel(false)
                        } else {
                            val editor: Editor? = ctx.getData(PlatformDataKeys.EDITOR)
                            var type: String?
                            val projectName = project.name
                            // Active editor is required
                            if (editor != null) {
                                val settings = TestausTimeSettingsState.instance;
                                val client = TestausTimeApiClient(settings.apiBaseUrl, settings.authToken);
                                type = PsiUtilBase.getPsiFileInEditor(editor, project)?.fileType?.displayName
                                println(type)
                                println(projectName)
                                println(appName)
                                println(hostname)
                                val resp = client.activityLog(ActivityPostPayload(
                                    programmingLanguage = type,
                                    projectName = projectName,
                                    IDEName = appName,
                                    host = hostname
                                )).execute()
                                println(resp.message)
                                println(resp.code)
                                println(resp.body?.string())
                            }
                        }
                    }
                }
                dataContext.onError { err ->
                    println(err)
                }
            } catch (e: Exception) {
                println(e.message)
            }

        }, 0, 30, TimeUnit.SECONDS)
        println("ScheduledPinger registered!")
    }
}
