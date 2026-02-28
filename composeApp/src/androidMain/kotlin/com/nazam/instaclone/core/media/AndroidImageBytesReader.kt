package com.nazam.instaclone.core.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

/**
 * Android : lit une Uri (content://...) et renvoie des bytes JPEG compressés.
 *
 * Objectif :
 * - réduire la taille (upload plus fiable)
 * - rester simple et stable
 */
class AndroidImageBytesReader(
    private val appContext: Context
) : ImageBytesReader {

    override suspend fun readBytes(uri: String): ByteArray = withContext(Dispatchers.IO) {
        val parsed = Uri.parse(uri)

        val original = decodeBitmap(parsed)
        val resized = original.resizeMax(maxSize = 1440)

        resized.toJpegBytes(quality = 85)
    }

    private fun decodeBitmap(uri: Uri): Bitmap {
        // 1) Lire juste la taille
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        appContext.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, bounds)
        }

        // 2) Calculer un sample pour éviter OOM
        val sample = computeInSampleSize(bounds.outWidth, bounds.outHeight, 1440)

        // 3) Decoder vraiment
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        val bitmap = appContext.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, opts)
        }

        return bitmap ?: error("Impossible d'ouvrir l'image: $uri")
    }

    private fun computeInSampleSize(w: Int, h: Int, max: Int): Int {
        var inSampleSize = 1
        var halfW = w / 2
        var halfH = h / 2

        while (halfW / inSampleSize >= max && halfH / inSampleSize >= max) {
            inSampleSize *= 2
        }
        return inSampleSize
    }

    private fun Bitmap.resizeMax(maxSize: Int): Bitmap {
        val maxSide = maxOf(width, height)
        if (maxSide <= maxSize) return this

        val ratio = maxSize.toFloat() / maxSide.toFloat()
        val newW = (width * ratio).roundToInt().coerceAtLeast(1)
        val newH = (height * ratio).roundToInt().coerceAtLeast(1)

        return Bitmap.createScaledBitmap(this, newW, newH, true)
    }

    private fun Bitmap.toJpegBytes(quality: Int): ByteArray {
        val out = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, quality.coerceIn(0, 100), out)
        return out.toByteArray()
    }
}
