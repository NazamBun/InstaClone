package com.nazam.instaclone.feature.auth.domain.model

data class AuthUser(
    val id: String,
    val email: String,
    val displayName: String?
)