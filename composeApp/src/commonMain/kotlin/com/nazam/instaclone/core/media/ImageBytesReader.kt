package com.nazam.instaclone.core.media

/**
 * Lit une image locale (ex: "content://...") et renvoie ses bytes.
 *
 * KMP friendly :
 * - commonMain définit le contrat
 * - androidMain fait la vraie lecture
 * - iosMain pourra être fait plus tard
 */
interface ImageBytesReader {
    suspend fun readBytes(uri: String): ByteArray
}