package com.nazam.instaclone.core.media

/**
 * iOS : on fera plus tard.
 * Pour l'instant on bloque proprement.
 */
class IosImageBytesReader : ImageBytesReader {
    override suspend fun readBytes(uri: String): ByteArray {
        error("ImageBytesReader iOS pas encore implémenté")
    }
}