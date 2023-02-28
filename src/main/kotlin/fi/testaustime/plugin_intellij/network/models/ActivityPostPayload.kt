package fi.testaustime.plugin_intellij.network.models

import com.google.gson.annotations.SerializedName
import com.intellij.openapi.project.Project
import fi.testaustime.plugin_intellij.utils.ContextInformation
import fi.testaustime.plugin_intellij.utils.ContextInformation.getFriendlyName

data class ActivityPostPayload(
    @SerializedName("hostname") val hostname: String?,
    @SerializedName("language") val programmingLanguage: String?,
    @SerializedName("project_name") val projectName: String,
    @SerializedName("editor_name") val application: String,
) {
    companion object {
        fun fromProject(project: Project): ActivityPostPayload {
            return ActivityPostPayload(
                hostname = ContextInformation.getHostname(),
                programmingLanguage = ContextInformation.getProgrammingLanguage(),
                application = ContextInformation.getApplicationName(),
                projectName = project.getFriendlyName()
            )
        }
    }
}