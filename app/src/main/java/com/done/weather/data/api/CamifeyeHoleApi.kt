package com.done.weather.data.api

import com.done.weather.data.api.dto.ApiResponse
import com.done.weather.data.api.dto.CamifeyeHoleDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

class CamifeyeHoleApi(private val client: HttpClient) {

    suspend fun getHoles(): Result<List<CamifeyeHoleDto>> = runCatching {
        val response: HttpResponse = client.get("holes/")

        // 🔹 1. СНАЧАЛА пробуем как массив (если endpoint реально работает)
        runCatching {
            response.body<List<CamifeyeHoleDto>>()
        }.getOrElse {
            // 🔹 2. Если не массив — значит пришёл объект ошибки
            val error = response.body<ApiResponse<Unit>>()
            throw IllegalStateException(
                error.message ?: "Camifeye hole endpoint error"
            )
        }
    }
}

