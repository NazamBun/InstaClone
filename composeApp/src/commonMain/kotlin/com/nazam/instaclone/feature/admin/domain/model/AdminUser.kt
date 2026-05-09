package com.nazam.instaclone.feature.admin.domain.model

data class AdminUser(
    val id: String,
    val email: String? = null,
    val displayName: String?,
    val canCreatePost: Boolean,
    val createdAt: String? = null,
)
