package com.done.weather.di

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.done.weather.BuildConfig
import com.done.weather.data.api.CamifeyeRoundsApi
import com.done.weather.data.api.CamifeyeSettingsApi
import com.done.weather.data.api.AuthApiImpl
import com.done.weather.domain.WifiInfo
import com.done.weather.domain.repository.BaseSettingsRepository
import info.verifeye.vgps.data.api.CamifeyeHoleApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
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
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging

private object Names {
    const val DEFAULT_HTTP = "DefaultHttpClient"
    const val AUTHORIZED_HTTP = "AuthorizedHttpClient"
    const val AUTHORIZED_CAMIFEYE_HTTP = "AuthorizedCamifeyeHttpClient"
}

private const val BASE_CAMIFEYE = "/api-camifeye/"
private const val BASE_VGPS = "/api-vgps/"

val networkModule = module {

    // ✅ DEFAULT client (для /authenticate). ВАЖНО: он должен знать base url
    single<HttpClient>(named(Names.DEFAULT_HTTP)) {
        createDefaultHttpClient(get())
    }

    // /authenticate — как у тебя, не меняю
    single<AuthApi> { AuthApiImpl(client = get(named(Names.DEFAULT_HTTP))) }

    // ✅ старый authorized (vgps) — оставляем
    single<HttpClient>(named(Names.AUTHORIZED_HTTP)) {
        createAuthorizedHttp(
            context = get(),
            httpClient = get(named(Names.DEFAULT_HTTP)),
            authRepository = get(),
            serverAddressDatastore = get(),
            basePath = BASE_VGPS
        )
    }

    // ✅ новый authorized (camifeye)
    single<HttpClient>(named(Names.AUTHORIZED_CAMIFEYE_HTTP)) {
        createAuthorizedHttp(
            context = get(),
            httpClient = get(named(Names.DEFAULT_HTTP)),
            authRepository = get(),
            serverAddressDatastore = get(),
            basePath = BASE_CAMIFEYE
        )
    }

    // camifeye api
    single { CamifeyeSettingsApi(get(named(Names.AUTHORIZED_CAMIFEYE_HTTP))) }
    single { CamifeyeHoleApi(get(named(Names.AUTHORIZED_CAMIFEYE_HTTP))) }
    single { CamifeyeRoundsApi(get(named(Names.AUTHORIZED_CAMIFEYE_HTTP))) }
}

private val networkJsonSerializer by lazy {
    Json {
        encodeDefaults = true
        prettyPrint = false
        ignoreUnknownKeys = true
    }
}

private fun createDefaultHttpClient(
    baseSettingsRepository: BaseSettingsRepository
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
        requestTimeoutMillis = 30000
        connectTimeoutMillis = 15000
        socketTimeoutMillis = 15000
    }

    install(DefaultRequest) {
        header(HttpHeaders.Accept, ContentType.Application.Json)
        header(HttpHeaders.ContentType, ContentType.Application.Json)

        val server = runBlocking {
            baseSettingsRepository.serverAddress.first()
                .trim()
                .removeSuffix("/")
        }

        url {
            takeFrom(server)
            encodedPath = BASE_CAMIFEYE // <-- ВОТ ЭТО КЛЮЧЕВО
        }
    }
}

/**
 * ✅ Authorized клиент (и для vgps и для camifeye) — один и тот же "прицеп"
 * basePath задаём параметром (/api-vgps/ или /api-camifeye/)
 */
fun createAuthorizedHttp(
    context: Context,
    httpClient: HttpClient,
    authRepository: AuthRepository,
    serverAddressDatastore: BaseSettingsRepository,
    basePath: String
): HttpClient {
    return httpClient.config {

        install(ContentNegotiation) { json(networkJsonSerializer) }

        install(Auth) {
            bearer {

                // если токен пустой — Ktor не добавит пустой Authorization
                loadTokens {
                    val token = runBlocking { serverAddressDatastore.bearerToken.first() }.trim()
                    if (token.isBlank()) null else BearerTokens(token, "")
                }

                // ✅ ТВОЙ refresh — 1:1
                refreshTokens {
                    val bearer = runBlocking {
                        authRepository.authenticate(WifiInfo.getDeviceId(context)).fold(
                            onSuccess = { token ->
                                serverAddressDatastore.updateBearerToken(token)
                                token
                            },
                            onFailure = {
                                Timber.tag("Auth").e(it, "Failed to refresh token")
                                throw it
                            }
                        )
                    }
                    BearerTokens(bearer, "")
                }

                // ✅ чтобы auth мог попытаться обновиться не только после 401
                // (и чтобы не было "Bearer " пустого)
                sendWithoutRequest { true }
            }
        }

        install(DefaultRequest) {
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header(HttpHeaders.ContentType, ContentType.Application.Json)

            val server = runBlocking {
                serverAddressDatastore.serverAddress.first()
                    .trim()
                    .removeSuffix("/")
            }

            url {
                takeFrom(server)
                encodedPath = basePath // "/api-vgps/" или "/api-camifeye/"
            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
val jsonDeserializer by lazy {
    Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
       // decodeEnumsCaseInsensitive = true
    }
}
