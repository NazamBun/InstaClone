package com.nazam.instaclone.core.share

/**
 * Génère un lien partageable vers un post.
 * V1 : lien "web" simple.
 *
 * Plus tard : deep link + fallback store.
 */
object ShareLinkFactory {

    private const val BASE_URL = "https://instaclone.app/p"

    fun postLink(postId: String): String {
        val safeId = postId.trim()
        return "$BASE_URL/$safeId?utm_source=share&utm_medium=app"
    }
}
