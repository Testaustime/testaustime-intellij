package fi.testaustime.plugin_intellij.activity

import com.intellij.openapi.application.PreloadingActivity
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressIndicator
import fi.testaustime.plugin_intellij.services.TestaustimeApplicationService

internal class AppPreloadingActivity : PreloadingActivity() {
    override fun preload(indicator: ProgressIndicator) {
        service<TestaustimeApplicationService>()
    }
}