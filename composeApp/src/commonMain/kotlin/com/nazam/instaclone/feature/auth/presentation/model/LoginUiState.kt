package com.nazam.instaclone.feature.auth.presentation.model

import com.nazam.instaclone.core.ui.UiText

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: UiText? = null
)