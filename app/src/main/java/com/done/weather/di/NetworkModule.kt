package com.done.weather.di

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.done.weather.BuildConfig
import com.done.weather.api.AuthApi
import com.done.weather.data.api.AuthApiImpl
import com.done.weather.data.api.CamifeyeHoleApi
import com.done.weather.data.api.CamifeyeRoundsApi
import com.done.weather.data.api.CamifeyeSettingsApi
import com.done.weather.domain.WifiInfo
import com.done.weather.domain.repository.BaseSettingsRepository
import com.done.weather.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import timber.log.Timber

private object Names {
    const val DEFAULT_HTTP = "DefaultHttpClient"
    const val AUTHORIZED_VGPS_HTTP = "AuthorizedVgpsHttpClient"
    const val AUTHORIZED_CAMIFEYE_HTTP = "AuthorizedCamifeyeHttpClient"
}

private const val BASE_CAMIFEYE = "/api-camifeye/"
private const val BASE_VGPS = "/api-vgps/"

private val networkJsonSerializer = Json {
    encodeDefaults = true
    prettyPrint = false
    ignoreUnknownKeys = true
}

val networkModule = module {

    single<HttpClient>(named(Names.DEFAULT_HTTP)) { createDefaultHttpClient(get()) }

    single<AuthApi> { AuthApiImpl(client = get(named(Names.DEFAULT_HTTP))) }

    single<HttpClient>(named(Names.AUTHORIZED_VGPS_HTTP)) {
        createAuthorizedHttp(
            context = get(),
            baseClient = get(named(Names.DEFAULT_HTTP)),
            authRepository = get(),
            settings = get(),
            basePath = BASE_VGPS
        )
    }

    single<HttpClient>(named(Names.AUTHORIZED_CAMIFEYE_HTTP)) {
        createAuthorizedHttp(
            context = get(),
            baseClient = get(named(Names.DEFAULT_HTTP)),
            authRepository = get(),
            settings = get(),
            basePath = BASE_CAMIFEYE
        )
    }

    single { CamifeyeSettingsApi(get(named(Names.AUTHORIZED_CAMIFEYE_HTTP))) }
    single { CamifeyeHoleApi(get(named(Names.AUTHORIZED_CAMIFEYE_HTTP))) }
    single { CamifeyeRoundsApi(get(named(Names.AUTHORIZED_CAMIFEYE_HTTP))) }
}

private fun createDefaultHttpClient(
    settings: BaseSettingsRepository
) = HttpClient(OkHttp) {

    expectSuccess = false

    install(ContentNegotiation) { json(networkJsonSerializer) }

    install(Logging) {
        logger = object : Logger {
            @SuppressLint("LogNotTimber")
            override fun log(message: String) {
                Log.i("Ktor", message)
            }
        }
        level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 15_000
        socketTimeoutMillis = 15_000
    }

    // ✅ BASE URL ДЛЯ authenticate — как в старом
    install(DefaultRequest) {
        header(HttpHeaders.Accept, ContentType.Application.Json)
        header(HttpHeaders.ContentType, ContentType.Application.Json)

        val server = runBlocking { settings.serverAddress.first().trim().removeSuffix("/") }

        url {
            takeFrom(server)
            encodedPath = BASE_CAMIFEYE
        }
    }
}

private fun createAuthorizedHttp(
    context: Context,
    baseClient: HttpClient,
    authRepository: AuthRepository,
    settings: BaseSettingsRepository,
    basePath: String
): HttpClient {
    return baseClient.config {

        install(ContentNegotiation) { json(networkJsonSerializer) }

        install(Auth) {
            bearer {

                loadTokens {
                    val token = runBlocking { settings.bearerToken.first() }.trim()
                    if (token.isBlank()) null else BearerTokens(token, "")
                }

                refreshTokens {
                    Timber.tag("Auth").w("refreshTokens: START")

                    val newToken = runBlocking {
                        authRepository.authenticate(WifiInfo.getDeviceId(context))
                            .getOrElse { e ->
                                Timber.tag("Auth").e(e, "refreshTokens FAILED")
                                throw e
                            }
                    }

                    settings.updateBearerToken(newToken)
                    Timber.tag("Auth").d("refreshTokens: SUCCESS token=${newToken.take(10)}...")

                    BearerTokens(newToken, "")
                }

                // ✅ КЛЮЧ: как в старом проекте — пробуем авторизоваться ДО запроса
                // но authenticate НЕ трогаем
                sendWithoutRequest { request ->
                    !request.url.encodedPath.contains("authenticate", ignoreCase = true)
                }
            }
        }

        install(DefaultRequest) {
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header(HttpHeaders.ContentType, ContentType.Application.Json)

            val server = runBlocking { settings.serverAddress.first().trim().removeSuffix("/") }

            url {
                takeFrom(server)
                encodedPath = basePath
            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
val jsonDeserializer by lazy {
    Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        decodeEnumsCaseInsensitive = true
    }
}
