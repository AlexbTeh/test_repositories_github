package com.done.weather.domain.model

import com.done.weather.domain.model.ApiResult.Success

sealed class ApiResult<out T, out E> {
    data class Success<T>(val data: T) : ApiResult<T, Nothing>()

    sealed class Error<E> : ApiResult<Nothing, E>() {
        abstract val exception: Throwable

        data class NetworkError<E>(override val exception: Throwable) : Error<E>()
        data class ServerError<E>(
            override val exception: Throwable,
            val statusCode: Int,
            val data: E?
        ) : Error<E>()

        data class UnknownError<E>(override val exception: Throwable) : Error<E>()
    }

    fun getOrNull(): T? {
        return when (this) {
            is Success -> data
            else -> null
        }
    }

    inline fun <R> fold(
        onSuccess: (T) -> R,
        onError: (Error<out E>) -> R
    ): R {
        return when (this) {
            is Success -> onSuccess(data)
            is Error -> onError(this)
        }
    }
}

inline fun <T, E> ApiResult<T, E>.onSuccess(action: (T) -> Unit): ApiResult<T, E> {
    if (this is Success) {
        action(data)
    }
    return this
}

inline fun <T, E> ApiResult<T, E>.onError(action: (ApiResult.Error<E>) -> Unit): ApiResult<T, E> {
    if (this is ApiResult.Error) {
        action(this)
    }
    return this
}

inline fun <T, E, RT, RE> ApiResult<T, E>.map(
    onSuccess: (T) -> RT,
    onError: (E?) -> RE?
): ApiResult<RT, RE> {
    return when (this) {
        is Success -> Success(onSuccess(data))
        is ApiResult.Error.NetworkError -> ApiResult.Error.NetworkError(exception)
        is ApiResult.Error.ServerError -> ApiResult.Error.ServerError(
            exception,
            statusCode,
            onError(data)
        )

        is ApiResult.Error.UnknownError -> ApiResult.Error.UnknownError(exception)
    }
}