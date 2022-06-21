package fi.testaustime.plugin_intellij.network

import com.google.gson.Gson
import fi.testaustime.plugin_intellij.network.models.ActivityPostPayload
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class TestausTimeApiClient(private val baseUrl: String, token: String) {
    private val httpClient = getHttpClient(token)

    companion object HTTPClient {
        fun getHttpClient(token: String): OkHttpClient {
            return OkHttpClient.Builder().addInterceptor(Interceptor {chain: Interceptor.Chain ->
                val request = chain.request()
                val newBuilder = request.newBuilder();
                newBuilder.addHeader("User-Agent", "TestausTimeClient-IntelliJ")
                    .addHeader("Authorization", "Bearer $token")
                    .addHeader("Content-Type", "application/json");
                chain.proceed(newBuilder.build())
            }).build()
        }
    }


    fun activityLog(payload: ActivityPostPayload): Call {
        val request = Request.Builder().url("$baseUrl/activity/update").method("POST", RequestBody.Companion.create(
            "application/json".toMediaTypeOrNull(), Gson().toJson(payload)
        ))
        return httpClient.newCall(request.build())
    }

}