package fi.testaustime.plugin_intellij.network

import com.google.gson.Gson
import fi.testaustime.plugin_intellij.network.models.ActivityPostPayload
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest.BodyPublishers.noBody
import java.net.http.HttpRequest.BodyPublishers.ofString
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers.discarding
import java.net.http.HttpResponse.BodyHandlers.ofString
import java.time.Duration.ofSeconds
import java.util.concurrent.CompletableFuture

class TestaustimeAPIClient(private val baseUrl: String, token: String) {
    private val headers = arrayOf(
        "User-Agent", USER_AGENT,
        "Content-Type", "application/json",
        "Authorization", "Bearer $token"
    )

    fun flushActivity(): CompletableFuture<HttpResponse<Void>> {
        return HTTP_CLIENT.sendAsync(
            newBuilder(URI("$baseUrl/activity/flush"))
                .POST(noBody()).headers(*headers).build(), discarding()
        )
    }

    fun activityLog(payload: ActivityPostPayload): CompletableFuture<HttpResponse<String>> {
        return HTTP_CLIENT.sendAsync(
            newBuilder(URI("$baseUrl/activity/update"))
                .POST(ofString(Gson().toJson(payload)))
                .headers(*headers).build(),
            ofString()
        );
    }

    fun me(): CompletableFuture<HttpResponse<String>> {
        return HTTP_CLIENT.sendAsync(
            newBuilder(URI("$baseUrl/users/@me")).GET().headers(*headers).build(),
            ofString()
        );
    }

    companion object {
        const val USER_AGENT = "Intellij-Testaustime"
        val HTTP_CLIENT: HttpClient = HttpClient.newBuilder()
            .connectTimeout(ofSeconds(10L)).build();

        fun verifyToken(baseUrl: String, token: String): Boolean {
            try {
                val conn: HttpURLConnection = URL(baseUrl).openConnection() as HttpURLConnection
                conn.disconnect()
            } catch (ex: Exception) {
                return true;
            }
            val request = newBuilder(URI("$baseUrl/users/@me")).GET().headers(
                "Authorization", "Bearer $token",
                "User-Agent", USER_AGENT
            ).build()
            val resp = HTTP_CLIENT.send(request, discarding());
            return resp.statusCode() != 401;
        }
    }

}