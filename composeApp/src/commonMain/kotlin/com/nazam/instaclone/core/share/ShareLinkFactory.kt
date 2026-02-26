package com.nazam.instaclone.core.share

/**
 * Fabrique un lien de partage.
 * V1: lien "fake" (plus tard: deep link + fallback web).
 */
object ShareLinkFactory {

    private const val BASE_URL = "https://instaclone.app/p"

    fun postLink(postId: String, code: String): String {
        val safeId = postId.trim()
        val safeCode = code.trim()
        return "$BASE_URL/$safeId?utm_source=share&utm_medium=app&c=$safeCode"
    }
}
