package com.done.weather.data.api
import com.done.weather.data.api.dto.CamifeyeRoundCreateDto
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

class CamifeyeRoundsApi(private val client: HttpClient) {

    suspend fun postRound(payload: CamifeyeRoundCreateDto): Result<Unit> = runCatching {
        val response: HttpResponse = client.post("round/") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }

        val code = response.status.value
        if (code !in 200..299) {
            val bodyText = response.bodyAsText() // тут будет текст ошибки от сервера (если есть)
            throw ApiHttpException(code, bodyText)
        }

        // если 2xx — ок
        Unit
    }
}

class ApiHttpException(
    val code: Int,
    val responseBody: String
) : RuntimeException(
    buildString {
        append("HTTP ").append(code)
        if (responseBody.isNotBlank()) append(": ").append(responseBody)
    }
)
