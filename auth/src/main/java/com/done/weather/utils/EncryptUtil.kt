package com.done.weather.utils

import com.done.weather.model.EncryptionOutput
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

internal object EncryptUtil {
    private const val AAD_LENGTH = 16
    private const val TAG_LENGTH = 16
    private const val IV_LENGTH = 12

    fun encrypt(key: SecretKey, message: ByteArray): EncryptionOutput {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        val iv = ByteArray(IV_LENGTH)
        SecureRandom().nextBytes(iv)

        val spec = GCMParameterSpec(TAG_LENGTH * 8, iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)

        val aad = ByteArray(AAD_LENGTH)
        SecureRandom().nextBytes(aad)
        cipher.updateAAD(aad)

        val result = cipher.doFinal(message)

        val tagLengthInBytes = TAG_LENGTH
        val ciphertextLength = result.size - tagLengthInBytes

        val ciphertext = result.copyOfRange(0, ciphertextLength)
        val tag = result.copyOfRange(ciphertextLength, result.size)

        return EncryptionOutput(iv = iv, aad = aad, tag = tag, ciphertext = ciphertext)
    }
}