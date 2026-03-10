package com.done.weather.model

internal data class EncryptionOutput(
    val iv: ByteArray,
    val aad: ByteArray,
    val tag: ByteArray,
    val ciphertext: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptionOutput

        if (!iv.contentEquals(other.iv)) return false
        if (!aad.contentEquals(other.aad)) return false
        if (!tag.contentEquals(other.tag)) return false
        if (!ciphertext.contentEquals(other.ciphertext)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = iv.contentHashCode()
        result = 31 * result + aad.contentHashCode()
        result = 31 * result + tag.contentHashCode()
        result = 31 * result + ciphertext.contentHashCode()
        return result
    }
}