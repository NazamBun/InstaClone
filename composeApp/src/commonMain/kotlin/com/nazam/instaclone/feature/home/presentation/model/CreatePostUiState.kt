package com.nazam.instaclone.feature.home.presentation.model

data class CreatePostUiState(
    val question: String = "",
    val leftLabel: String = "",
    val rightLabel: String = "",
    val leftImageUrl: String = "",
    val rightImageUrl: String = "",
    val category: String = "",

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPostCreated: Boolean = false
)