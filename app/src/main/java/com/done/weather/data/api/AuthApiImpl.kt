package com.done.weather.data.api

import com.done.weather.api.AuthApi
import com.done.weather.data.api.dto.BaseResponseDto
import com.done.weather.data.safeApiCall
import com.done.weather.model.AuthParams
import com.done.weather.model.Token
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthApiImpl(private val client: HttpClient) :  AuthApi {
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