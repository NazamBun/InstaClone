package com.nazam.instaclone.feature.home.presentation.draft

object CreatePostDraftStore {

    data class Draft(
        val question: String = "",
        val leftLabel: String = "",
        val rightLabel: String = "",
        val leftLocalUri: String = "",
        val rightLocalUri: String = "",
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
