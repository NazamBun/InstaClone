package com.nazam.instaclone.feature.auth.presentation.model

import com.nazam.instaclone.core.ui.UiText

data class SignupUiState(
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val isLoading: Boolean = false,
    val isSignedUp: Boolean = false,
    val error: UiText? = null
)