package com.done.weather.data.api
import com.done.weather.data.api.dto.CamifeyeSettingsDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

class CamifeyeSettingsApi(private val client: HttpClient) {

    suspend fun getSettings(): Result<CamifeyeSettingsDto?> = runCatching {
        val response: HttpResponse = client.get("settings/")

        when (response.status) {
            HttpStatusCode.NotFound -> null
            HttpStatusCode.OK ->{
                response.body<CamifeyeSettingsDto>()
            } // ✅ settings есть
            else -> {

                val text = response.body<String>()
                error("getSettings failed: ${response.status}. Body=$text")
            }
        }
    }

    suspend fun postSettings(payload: CamifeyeSettingsDto): Result<Unit> = runCatching {
        client.post("settings/") { setBody(payload) }
        Unit
    }

    suspend fun putSettings(payload: CamifeyeSettingsDto): Result<Unit> = runCatching {
        client.put("settings/") { setBody(payload) }
        Unit
    }
}
