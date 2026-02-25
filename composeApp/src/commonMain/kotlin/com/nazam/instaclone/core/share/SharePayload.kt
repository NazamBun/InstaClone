package com.nazam.instaclone.core.share

/**
 * Données "simples" à partager.
 * ✅ KMP friendly
 */
data class SharePayload(
    val text: String,
    val subject: String? = null
)
