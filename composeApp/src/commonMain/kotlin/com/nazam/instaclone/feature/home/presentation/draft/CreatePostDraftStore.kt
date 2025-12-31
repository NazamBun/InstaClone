package com.nazam.instaclone.feature.home.presentation.draft

/**
 * Petit store en mémoire pour garder le formulaire CreatePost
 * quand on navigue vers un autre écran (ex: Categories).
 *
 * KMP friendly : pas d'Android Context, pas de DataStore ici.
 */
object CreatePostDraftStore {

    data class Draft(
        val question: String = "",
        val leftLabel: String = "",
        val rightLabel: String = "",
        val leftImageUrl: String = "",
        val rightImageUrl: String = "",
        val categoryId: String = ""
    )

    private var draft: Draft = Draft()

    fun get(): Draft = draft

    fun update(newDraft: Draft) {
        draft = newDraft
    }

    fun clear() {
        draft = Draft()
    }
}