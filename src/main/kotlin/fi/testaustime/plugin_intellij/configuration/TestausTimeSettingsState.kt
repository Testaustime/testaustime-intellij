package fi.testaustime.plugin_intellij.configuration

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.openapi.components.Storage
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable


@State(name = "fi.testaustime.plugin_intellij.configuration.TestausTimeSettingsState", storages = [Storage("TestausTimePlugin.xml")])
class TestausTimeSettingsState : PersistentStateComponent<TestausTimeSettingsState> {
    var apiBaseUrl = "https://api.testaustime.fi"
    var authToken = ""

    @Nullable
    override fun getState(): TestausTimeSettingsState {
        return this
    }

    override fun loadState(@NotNull state: TestausTimeSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val instance: TestausTimeSettingsState
            get() = ApplicationManager.getApplication().getService(TestausTimeSettingsState::class.java)
    }
}