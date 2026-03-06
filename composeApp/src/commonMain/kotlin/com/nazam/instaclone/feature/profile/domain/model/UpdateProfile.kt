package com.nazam.instaclone.feature.profile.domain.model

/**
 * Données à sauvegarder pour modifier le profil (Clean).
 */
data class UpdateProfile(
    val displayName: String,
    val username: String,
    val bio: String,
    val location: String,
    val website: String
)
