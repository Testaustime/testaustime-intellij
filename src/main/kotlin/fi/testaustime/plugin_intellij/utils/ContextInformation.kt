package fi.testaustime.plugin_intellij.utils

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.psi.util.PsiUtilBase
import com.kstruct.gethostname4j.Hostname
import kotlinx.coroutines.runBlocking
import org.jetbrains.concurrency.await

object ContextInformation {
    fun Project.getFriendlyName(): String {
        val moduleManager = ModuleManager.getInstance(this)
        val modules = moduleManager.modules
        for (module in modules) {
            if (!module.isLoaded) continue
            val moduleRootManager = ModuleRootManager.getInstance(module)
            val contentRoots = moduleRootManager.contentRoots
            if (contentRoots.contains(this.guessProjectDir())) {
                return module.name
            }
        }

        return this.name
    }

    fun getProgrammingLanguage(): String? {
        return ReadAction.compute<String?, Throwable> {
            val context = getDataContext()
            val editor = context?.getData(EDITOR)
            val project = editor?.project

            project?.let {
                val psiFile = PsiUtilBase.getPsiFileInEditor(editor, it)
                val language = psiFile?.language
                language?.displayName
            }
        }
    }

    fun getDataContext(): DataContext? {
        return runBlocking {
            DataManager.getInstance().dataContextFromFocusAsync.await()
        }
    }

    fun getApplicationName(): String {
        return ReadAction.compute<String, Throwable> {
            ApplicationInfo.getInstance().fullApplicationName
        }
    }

    fun getHostname(): String? = try {
        Hostname.getHostname()
    } catch (e: RuntimeException) {
        null
    }
}