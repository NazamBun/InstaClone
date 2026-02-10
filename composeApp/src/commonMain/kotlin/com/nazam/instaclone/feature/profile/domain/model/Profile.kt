package com.nazam.instaclone.feature.profile.domain.model

/**
 * Domaine (Clean Architecture)
 * => simple, sans Supabase / DTO / UI.
 */
data class Profile(
    val userId: String,
    val email: String,
    val displayName: String,
    val username: String,
    val bio: String,
    val location: String,
    val website: String,
    val avatarUrl: String?,
    val coverUrl: String?,
    val joinedLabel: String
)
