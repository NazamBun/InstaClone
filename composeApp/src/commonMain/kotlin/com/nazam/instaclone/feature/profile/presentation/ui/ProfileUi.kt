package com.nazam.instaclone.feature.profile.presentation.ui

import com.nazam.instaclone.feature.home.domain.model.VsPost

data class ProfileUi(
    val userId: String,
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
    val isSelfProfile: Boolean = false,
    val isFollowing: Boolean = false,
    val isFollowLoading: Boolean = false
)
