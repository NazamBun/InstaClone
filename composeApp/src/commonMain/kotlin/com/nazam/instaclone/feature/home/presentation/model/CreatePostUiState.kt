package com.nazam.instaclone.feature.home.presentation.model

import com.nazam.instaclone.core.ui.UiText

/**
 * Etat UI pour CreatePost.
 *
 * IMPORTANT :
 * - URIs locales (content://...) : pour afficher l'aperçu
 * - URLs uploadées (https://...) : pour envoyer au backend
 * - 2 flags d'upload (gauche / droite)
 *
 * ✅ Le ViewModel calcule :
 * - isSubmitEnabled
 * - submitBlockedReason
 *
 * ✅ Tous les textes UI passent par UiText (pour les traductions).
 */
data class CreatePostUiState(
    val question: String = "",
    val leftLabel: String = "",
    val rightLabel: String = "",
    val category: String = "",

    // URIs locales (content://...)
    val leftLocalUri: String = "",
    val rightLocalUri: String = "",

    // URLs publiques après upload (https://...)
    val leftUploadedUrl: String = "",
    val rightUploadedUrl: String = "",

    // Upload en cours
    val isUploadingLeft: Boolean = false,
    val isUploadingRight: Boolean = false,

    val isLoading: Boolean = false,

    // Erreur “propre” (resource ou texte dynamique)
    val error: UiText? = null,

    // Info optionnelle
    val isPostCreated: Boolean = false,

    // Dérivés (calculés par le ViewModel)
    val isSubmitEnabled: Boolean = false,
    val submitBlockedReason: UiText? = null
)