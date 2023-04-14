package fi.testaustime.plugin_intellij.configuration

import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.ComponentValidator
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.Disposer
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import fi.testaustime.plugin_intellij.TestaustimeBundle.message
import fi.testaustime.plugin_intellij.network.TestaustimeAPIClient
import org.jetbrains.annotations.NotNull
import java.net.MalformedURLException
import java.net.URL
import java.util.function.Supplier
import javax.swing.InputVerifier
import javax.swing.JComponent
import javax.swing.JPanel


class SettingsComponent: Disposable {
    fun validate(): Boolean {
        var valid = baseUrlText.inputVerifier?.verify(baseUrlText) ?: false;
        valid = valid and (authTokenText.inputVerifier?.verify(authTokenText) ?: false);
        return valid
    }

    val panel: JPanel
    private val baseUrlText = JBTextField()
    private val authTokenText = JBTextField()

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel(message("settings.apiBaseURL") + ": "), baseUrlText, 1, false)
            .addLabeledComponent(JBLabel(message("settings.apiToken") + ": "), authTokenText, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel

        run {
            val validator = ComponentValidator(this).withValidator(Supplier {
                if (baseUrlText.text.endsWith("/")) {
                    return@Supplier ValidationInfo(message("settings.apiBaseURL.noSlash"), baseUrlText)
                } else {
                    try {
                        URL(baseUrlText.text)
                    } catch (ex: MalformedURLException) {
                        return@Supplier ValidationInfo(message("settings.apiBaseURL.mustBeURL"), baseUrlText)
                    }
                }

                return@Supplier ValidationInfo("").withOKEnabled();
            }).installOn(baseUrlText)

            baseUrlText.inputVerifier = object : InputVerifier() {
                override fun verify(input: JComponent?): Boolean {
                    validator.revalidate()
                    return validator.validationInfo?.okEnabled ?: true;
                }
            }
        }


        run {
            val validator = ComponentValidator(this).withValidator(Supplier {
                if (authTokenText.text.isEmpty()) {
                    return@Supplier ValidationInfo(message("settings.apiToken.needToken"), authTokenText).asWarning();
                }

                if (!authTokenText.text.chars().allMatch(Character::isLetterOrDigit)) {
                    return@Supplier ValidationInfo(message("settings.apiToken.mustBeAlphanumeric"), authTokenText);
                }

                if (authTokenText.text.length != 32) {
                    return@Supplier ValidationInfo(message("settings.apiToken.invalidLength", authTokenText.text.length),
                        authTokenText);
                }

                if (!TestaustimeAPIClient.verifyToken(baseUrlText.text, authTokenText.text)) {
                    return@Supplier ValidationInfo(message("settings.apiToken.invalid", baseUrl ?: "The API"), authTokenText);
                }

                return@Supplier ValidationInfo("").withOKEnabled();
            }).installOn(authTokenText)

            authTokenText.inputVerifier = object : InputVerifier() {
                override fun verify(input: JComponent?): Boolean {
                    validator.revalidate()
                    validator.validationInfo?.let {
                        return it.okEnabled or it.warning;
                    }
                    return false;
                }
            }
        }
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

    override fun dispose() {

    }
}