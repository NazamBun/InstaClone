package com.nazam.instaclone.feature.profile.presentation.ui

import com.nazam.instaclone.feature.home.domain.model.VsPost

/**
 * Modèle UI (presentation)
 * - utilisé par ProfileScreen + ProfileUiState + ProfileViewModel
 * - KMP friendly
 */
data class ProfileUi(
    val displayName: String,
    val username: String,
    val bio: String,
    val location: String,
    val website: String,
    val joinedLabel: String,
    val postsCount: Int,
    val followersCount: Int,
    val followingCount: Int,
    val avatarUrl: String? = null,
    val coverUrl: String? = null,
    val posts: List<VsPost> = emptyList(),

    /** true si c'est mon profil */
    val isSelfProfile: Boolean = false
)
