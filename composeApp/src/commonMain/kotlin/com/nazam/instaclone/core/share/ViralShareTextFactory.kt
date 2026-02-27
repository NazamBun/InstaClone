package com.nazam.instaclone.core.share

import com.nazam.instaclone.feature.home.domain.model.VsPost

/**
 * Texte "viral" :
 * - Hook très court
 * - Duel A/B
 * - Code court (effet curiosité)
 * - Lien
 *
 * ✅ KMP friendly
 */
object ViralShareTextFactory {

    data class Content(
        val payload: SharePayload,
        val link: String,
        val code: String
    )

    fun contentFromPost(post: VsPost): Content {
        val code = shortCode(post.id)
        val hook = pickHook(post.id)

        val link = ShareLinkFactory.postLink(
            postId = post.id,
            code = code
        )

        val text = buildString {
            append(hook).append("\n\n")
            append(post.question.trim()).append("\n")
            append("A) ").append(post.leftLabel.trim()).append("\n")
            append("B) ").append(post.rightLabel.trim()).append("\n\n")
            append("Vote en 2s → ").append(link).append("\n")
            append("Code: ").append(code)
        }

        return Content(
            payload = SharePayload(text = text, subject = "VS"),
            link = link,
            code = code
        )
    }

    private fun pickHook(seed: String): String {
        return when (kotlin.math.abs(seed.hashCode()) % 4) {
            0 -> "🔥 A ou B ?"
            1 -> "😅 Choisis vite..."
            2 -> "⚽ Team A ou Team B ?"
            else -> "🤯 Tu vas hésiter..."
        }
    }

    private fun shortCode(seed: String): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val h = kotlin.math.abs(seed.hashCode())

        fun pick(i: Int): Char = chars[(h shr (i * 5)) % chars.length]

        return buildString {
            append(pick(0)); append(pick(1)); append(pick(2)); append(pick(3))
        }
    }
}
