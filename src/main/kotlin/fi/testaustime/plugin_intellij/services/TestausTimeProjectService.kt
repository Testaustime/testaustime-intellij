package fi.testaustime.plugin_intellij.services

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiUtilBase
import com.intellij.util.concurrency.AppExecutorUtil
import fi.testaustime.plugin_intellij.TestausTimeBundle
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
        scheduledPingTask = AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay({
            try {
                val hostname = InetAddress.getLocalHost().hostName
                val appName = ApplicationInfo.getInstance().fullApplicationName
                val dataContext = DataManager.getInstance().dataContextFromFocusAsync
                dataContext.onSuccess { ctx ->
                    val project: Project? = ctx.getData(PlatformDataKeys.PROJECT)
                    if (project?.isOpen == true) {
                       val editor: Editor? = ctx.getData(PlatformDataKeys.EDITOR)
                        var type: String? = null
                        if (editor != null) {
                            type = PsiUtilBase.getPsiFileInEditor(editor, project)?.fileType?.displayName
                        }
                        val projectName = project.name
                        println(type)
                        println(projectName)
                        println(appName)
                        println(hostname)
                    }
                }
                dataContext.onError { err ->
                    println(err)
                }
            } catch (e: Exception) {
                println(e.message)
            }

        }, 0, 5, TimeUnit.SECONDS)
        println("ScheduledPinger registered!")
    }
}
