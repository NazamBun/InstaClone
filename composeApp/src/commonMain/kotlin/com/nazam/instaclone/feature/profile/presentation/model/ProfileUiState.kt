package com.nazam.instaclone.feature.profile.presentation.model

import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.profile.presentation.ui.ProfileUi

data class ProfileUiState(
    val isLoading: Boolean = false,
    val ui: ProfileUi? = null,
    val error: UiText? = null
)
