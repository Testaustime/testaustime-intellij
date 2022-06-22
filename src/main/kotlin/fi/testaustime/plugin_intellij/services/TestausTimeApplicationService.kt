package fi.testaustime.plugin_intellij.services

import com.intellij.openapi.diagnostic.Logger
import fi.testaustime.plugin_intellij.TestausTimeBundle
import org.jetbrains.rpc.LOG

class TestausTimeApplicationService {

    init {
        Logger.getInstance("#Testaustime").debug(TestausTimeBundle.message("applicationService"))
    }
}
