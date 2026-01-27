package com.nazam.instaclone.feature.home.presentation.model

/**
 * Etat UI pour CreatePost.
 *
 * IMPORTANT :
 * - On garde les URIs locales (content://...) pour afficher l'aperçu.
 * - On garde les URLs uploadées (https://...) pour envoyer au backend.
 * - On a 2 flags d'upload (gauche / droite).
 */
data class CreatePostUiState(
    val question: String = "",
    val leftLabel: String = "",
    val rightLabel: String = "",
    val category: String = "",

    // ✅ URIs locales (content://...)
    val leftLocalUri: String = "",
    val rightLocalUri: String = "",

    // ✅ URLs publiques après upload (https://...)
    val leftUploadedUrl: String = "",
    val rightUploadedUrl: String = "",

    // ✅ Upload en cours
    val isUploadingLeft: Boolean = false,
    val isUploadingRight: Boolean = false,

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPostCreated: Boolean = false
)