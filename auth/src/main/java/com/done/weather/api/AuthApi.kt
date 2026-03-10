package com.done.weather.api

import com.done.weather.model.AuthParams
import com.done.weather.model.Token

interface AuthApi {
    suspend fun authenticate(firstToken: String, authParams: AuthParams): Result<Token>
}