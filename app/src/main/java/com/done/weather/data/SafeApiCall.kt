package com.done.weather.data

import com.done.weather.domain.model.ApiResult
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException


suspend inline fun <reified T, reified E> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    crossinline apiCall: suspend () -> T
): ApiResult<T, E> {
    return try {
        ApiResult.Success(withContext(dispatcher) { apiCall() })
    } catch (exception: Exception) {
        when (exception) {
            is IOException -> ApiResult.Error.NetworkError(exception)
            is ResponseException -> {
                val data: E? = runCatching {
                    exception.response.body<E>()
                }.getOrNull()

                ApiResult.Error.ServerError(
                    exception,
                    exception.response.status.value,
                    data
                )
            }

            else -> ApiResult.Error.UnknownError(exception)
        }
    }
}

