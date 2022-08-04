package fi.testaustime.plugin_intellij.configuration

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import fi.testaustime.plugin_intellij.TestaustimeBundle
import fi.testaustime.plugin_intellij.services.TestaustimeApplicationService
import org.jetbrains.annotations.Nullable
import javax.swing.JComponent



class ConfigurableSettings : Configurable {
    private var settingsComponent: SettingsComponent? = null

    @Nullable
    override fun createComponent(): JComponent? {
        settingsComponent = SettingsComponent()
        return settingsComponent?.panel
    }

    override fun isModified(): Boolean {
        val settings: SettingsState = SettingsState.instance
        var modified: Boolean = !settingsComponent?.baseUrl.equals(settings.apiBaseUrl)
        modified = modified or (!settingsComponent?.authToken.equals(settings.authToken))
        modified = modified and (settingsComponent?.validate() ?: false)
        return modified
    }


    override fun apply() {
        val settings: SettingsState = SettingsState.instance
        settings.apiBaseUrl = settingsComponent?.baseUrl ?: "https://api.testaustime.fi"
        settings.authToken = settingsComponent?.authToken ?: ""

        // Validate token immediately
        service<TestaustimeApplicationService>().pingNow();
    }

    override fun reset() {
        val settings: SettingsState = SettingsState.instance
        settingsComponent?.baseUrl = settings.apiBaseUrl
        settingsComponent?.authToken = settings.authToken
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }

    override fun getDisplayName(): String {
        return TestaustimeBundle.message("name");
    }
}