package com.nazam.instaclone.feature.home.presentation.draft

import com.nazam.instaclone.feature.home.domain.model.VoteCategories

/**
 * Petit store en mémoire pour garder le formulaire CreatePost
 * quand on navigue vers un autre écran (ex: Categories).
 *
 * KMP friendly : pas d'Android Context, pas de DataStore ici.
 *
 * ✅ Par défaut : catégorie Football
 */
object CreatePostDraftStore {

    data class Draft(
        val question: String = "",
        val leftLabel: String = "",
        val rightLabel: String = "",
        val categoryId: String = VoteCategories.FOOTBALL_ID,

        // ✅ URIs locales (content://...)
        val leftLocalUri: String = "",
        val rightLocalUri: String = "",

        // ✅ URLs après upload
        val leftUploadedUrl: String = "",
        val rightUploadedUrl: String = ""
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
