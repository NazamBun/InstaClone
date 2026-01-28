package com.nazam.instaclone.feature.home.presentation.model

/**
 * Etat UI pour CreatePost.
 *
 * IMPORTANT :
 * - URIs locales (content://...) : pour afficher l'aperçu
 * - URLs uploadées (https://...) : pour envoyer au backend
 * - 2 flags d'upload (gauche / droite)
 *
 * ✅ Etape 2 :
 * - isSubmitEnabled : le bouton "Publier" doit-il être cliquable ?
 * - submitBlockedReason : si bloqué, on affiche pourquoi (message simple)
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
    val isPostCreated: Boolean = false,

    // ✅ Dérivés (calculés par le ViewModel)
    val isSubmitEnabled: Boolean = false,
    val submitBlockedReason: String? = null
)