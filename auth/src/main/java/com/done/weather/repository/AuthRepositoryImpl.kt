package com.done.weather.repository

import com.done.weather.api.AuthApi
import com.done.weather.auth.BuildConfig
import com.done.weather.model.AuthParams
import com.done.weather.utils.EncryptUtil
import com.done.weather.utils.TokenGenerateUtil
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


internal class AuthRepositoryImpl(private val authApi: AuthApi) : AuthRepository {
    override suspend fun authenticate(deviceId: String): Result<String> {
        return authApi.authenticate(
            firstToken = "Bearer ${TokenGenerateUtil.generateFirstToken()}",
            encryptDeviceId(deviceId)
        ).map { it.token }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun encryptDeviceId(deviceId: String): AuthParams {
        val keyString = Base64.decode(BuildConfig.SECRET_KEY)
        val originalKey: SecretKey = SecretKeySpec(keyString, 0, keyString.size, "AES")

        val encodeResult = EncryptUtil.encrypt(
            originalKey, deviceId.toByteArray(
                Charsets.UTF_8
            )
        )

        return AuthParams(
            ciphertext = Base64.encode(encodeResult.ciphertext),
            encodedIv = Base64.encode(encodeResult.iv),
            encodedAad = Base64.encode(encodeResult.aad),
            encodedTag = Base64.encode(encodeResult.tag)
        )
    }
}