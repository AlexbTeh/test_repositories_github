package com.done.weather.data.api

import info.verifeye.auth.model.AuthParams
import info.verifeye.auth.model.Token
import com.done.weather.data.api.dto.BaseResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthApiImpl(private val client: HttpClient) : info.verifeye.auth.api.AuthApi {
    override suspend fun authenticate(firstToken: String, authParams: AuthParams): Result<Token> {
        return safeApiCall<Token, BaseResponseDto> {
            client.post("authenticate") {
                setBody(authParams)
                headers.append("Authorization", firstToken)
            }.body()
        }.fold(
            onSuccess = { Result.success(it) },
            onError = { Result.failure(it.exception) }
        )
    }
}