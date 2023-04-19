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
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Platform
import com.sun.jna.platform.win32.Kernel32Util
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

    interface UnixCLibrary : Library {
        companion object {
            val INSTANCE: UnixCLibrary = Native.load("c", UnixCLibrary::class.java)
        }

        fun gethostname(name: ByteArray, len: Int): Int
    }

    fun getHostname(): String? {
        return if (Platform.isWindows()) {
            Kernel32Util.getComputerName();
        } else {
            val buffer = ByteArray(4097)
            val result = UnixCLibrary.INSTANCE.gethostname(buffer, buffer.size)
            if (result == 0) String(buffer).trim { it <= ' ' } else null
        }
    }
}