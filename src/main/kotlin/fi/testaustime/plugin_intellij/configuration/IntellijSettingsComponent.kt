package fi.testaustime.plugin_intellij.configuration

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import org.jetbrains.annotations.NotNull
import javax.swing.JComponent
import javax.swing.JPanel


class IntellijSettingsComponent {
    val panel: JPanel
    private val baseUrlText = JBTextField()
    private val authTokenText = JBTextField()

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Testaustime API Base URL: "), baseUrlText, 1, false)
            .addLabeledComponent(JBLabel("Testaustime Authentication token: "), authTokenText, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    val preferredFocusedComponent: JComponent
        get() = baseUrlText

    @get:NotNull
    var baseUrl: String?
        get() = baseUrlText.text
        set(newText) {
            baseUrlText.text = newText
        }

    @get:NotNull
    var authToken: String?
        get() = authTokenText.text
        set(newText) {
            authTokenText.text = newText
        }
}