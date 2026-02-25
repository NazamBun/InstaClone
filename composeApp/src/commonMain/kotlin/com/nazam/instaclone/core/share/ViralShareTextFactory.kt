package com.nazam.instaclone.core.share

import com.nazam.instaclone.feature.home.domain.model.VsPost

/**
 * Construit un texte de partage "curieux" + lien.
 * Pas de stringResource ici (simple et KMP).
 */
object ViralShareTextFactory {

    fun fromPost(post: VsPost): SharePayload {
        val link = ShareLinkFactory.postLink(post.id)

        val hook = if (post.id.hashCode() % 2 == 0) {
            "Tu vas choisir quoi ?"
        } else {
            "C'est chaud... A ou B ?"
        }

        val text = buildString {
            append(hook).append("\n\n")
            append(post.question.trim()).append("\n")
            append("A) ").append(post.leftLabel.trim()).append("\n")
            append("B) ").append(post.rightLabel.trim()).append("\n\n")
            append("Vote dans l'app :").append("\n")
            append(link)
        }

        return SharePayload(
            text = text,
            subject = "VS"
        )
    }
}
