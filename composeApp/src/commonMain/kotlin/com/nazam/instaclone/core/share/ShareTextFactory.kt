package com.nazam.instaclone.core.share

import com.nazam.instaclone.feature.home.domain.model.VsPost

/**
 * Construit le texte partagé.
 * V1: juste du texte (pas d'image, pas de lien).
 */
object ShareTextFactory {

    fun fromPost(post: VsPost): SharePayload {
        val text = buildString {
            append(post.question.trim())
            append("\n\n")
            append("A) ").append(post.leftLabel.trim())
            append("\n")
            append("B) ").append(post.rightLabel.trim())
        }

        return SharePayload(
            text = text,
            subject = "VS"
        )
    }
}
