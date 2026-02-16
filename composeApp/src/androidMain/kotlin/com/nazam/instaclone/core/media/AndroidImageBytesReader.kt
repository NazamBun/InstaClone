package com.nazam.instaclone.core.media

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android : lit une Uri de galerie (content://...) via ContentResolver.
 */
class AndroidImageBytesReader(
    private val appContext: Context
) : ImageBytesReader {

    override suspend fun readBytes(uri: String): ByteArray = withContext(Dispatchers.IO) {
        val parsed = Uri.parse(uri)
        val inputStream = appContext.contentResolver.openInputStream(parsed)
            ?: error("Impossible d'ouvrir l'image: $uri")

        inputStream.use { it.readBytes() }
    }
}