package com.nazam.instaclone.core.share

/**
 * Données à partager.
 * - text : toujours présent
 * - imagePng : optionnel (quand on veut un partage "viral" avec carte)
 *
 * ✅ KMP friendly
 */
data class SharePayload(
    val text: String,
    val subject: String? = null,
    val imagePng: ByteArray? = null,
    val imageMimeType: String = "image/png",
    val imageFileName: String = "vs.png"
)
