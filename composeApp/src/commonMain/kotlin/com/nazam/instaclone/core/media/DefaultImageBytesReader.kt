package com.nazam.instaclone.core.media

/**
 * Impl par défaut (commonMain).
 * Les plateformes (Android / iOS) doivent remplacer ce binding via leurs modules DI.
 */
class DefaultImageBytesReader : ImageBytesReader {
    override suspend fun readBytes(uri: String): ByteArray {
        error("ImageBytesReader non fourni par la plateforme")
    }
}