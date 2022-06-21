package fi.testaustime.plugin_intellij.configuration

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nullable
import javax.swing.JComponent



class IntellijSettingConfigurable : Configurable {
    private var settingsComponent: IntellijSettingsComponent? = null


    fun preferredFocusedComponent(): JComponent? {
        return settingsComponent?.preferredFocusedComponent;
    }

    @Nullable
    override fun createComponent(): JComponent? {
        settingsComponent = IntellijSettingsComponent()
        return settingsComponent?.panel
    }

    override fun isModified(): Boolean {
        val settings: TestausTimeSettingsState = TestausTimeSettingsState.instance
        var modified: Boolean = !settingsComponent?.baseUrl.equals(settings.apiBaseUrl)
        modified = modified or (!settingsComponent?.authToken.equals(settings.authToken))
        return modified
    }


    override fun apply() {
        val settings: TestausTimeSettingsState = TestausTimeSettingsState.instance
        settings.apiBaseUrl = settingsComponent?.baseUrl ?: "https://api.testaustime.fi"
        settings.authToken = settingsComponent?.authToken ?: ""
    }

    override fun reset() {
        val settings: TestausTimeSettingsState = TestausTimeSettingsState.instance
        settingsComponent?.baseUrl = settings.apiBaseUrl
        settingsComponent?.authToken = settings.authToken
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }

    override fun getDisplayName(): String {
        return "Testaustime Settings"
    }
}