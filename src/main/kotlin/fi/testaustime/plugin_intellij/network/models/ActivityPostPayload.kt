package fi.testaustime.plugin_intellij.network.models

import com.google.gson.annotations.SerializedName

class ActivityPostPayload(
    @SerializedName("language") val programmingLanguage: String?,
    @SerializedName("hostname") val host: String,
    @SerializedName("editor_name") val IDEName: String,
    @SerializedName("project_name") val projectName: String,
)