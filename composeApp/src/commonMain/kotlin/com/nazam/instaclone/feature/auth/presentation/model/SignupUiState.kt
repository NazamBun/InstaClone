package com.nazam.instaclone.feature.auth.presentation.model

data class SignupUiState(
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val isLoading: Boolean = false,
    val isSignedUp: Boolean = false,
    val errorMessage: String? = null
)