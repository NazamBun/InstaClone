package com.nazam.instaclone.core.deeplink

/**
 * Parse un lien du type :
 * https://instaclone.app/p/{postId}?...
 *
 * ✅ KMP friendly (pas de Android Uri, pas de iOS NSURL)
 */
object DeepLinkParser {

    data class Result(
        val postId: String
    )

    fun parse(url: String): Result? {
        val clean = url.trim()
        if (clean.isBlank()) return null

        // On cherche "/p/"
        val marker = "/p/"
        val idx = clean.indexOf(marker)
        if (idx < 0) return null

        val start = idx + marker.length
        if (start >= clean.length) return null

        // postId finit avant "?" ou fin de string
        val end = clean.indexOf('?', startIndex = start).let { q ->
            if (q >= 0) q else clean.length
        }

        val postId = clean.substring(start, end).trim()
        if (postId.isBlank()) return null

        return Result(postId = postId)
    }
}
