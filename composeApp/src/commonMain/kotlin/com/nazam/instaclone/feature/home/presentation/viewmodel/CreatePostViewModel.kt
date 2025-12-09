package com.nazam.instaclone.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nazam.instaclone.feature.home.domain.usecase.CreatePostUseCase
import com.nazam.instaclone.feature.home.presentation.model.CreatePostUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreatePostViewModel(
    private val createPostUseCase: CreatePostUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    // -----------------------------
    // 📝 changements de champs
    // -----------------------------

    fun onQuestionChange(value: String) {
        _uiState.update { it.copy(question = value, errorMessage = null) }
    }

    fun onLeftLabelChange(value: String) {
        _uiState.update { it.copy(leftLabel = value, errorMessage = null) }
    }

    fun onRightLabelChange(value: String) {
        _uiState.update { it.copy(rightLabel = value, errorMessage = null) }
    }

    fun onLeftImageUrlChange(value: String) {
        _uiState.update { it.copy(leftImageUrl = value, errorMessage = null) }
    }

    fun onRightImageUrlChange(value: String) {
        _uiState.update { it.copy(rightImageUrl = value, errorMessage = null) }
    }

    fun onCategoryChange(value: String) {
        _uiState.update { it.copy(category = value, errorMessage = null) }
    }

    // -----------------------------
    // ✅ validation + création
    // -----------------------------

    fun submitPost() {
        val state = _uiState.value

        // petite validation très simple
        if (state.question.isBlank()
            || state.leftLabel.isBlank()
            || state.rightLabel.isBlank()
            || state.leftImageUrl.isBlank()
            || state.rightImageUrl.isBlank()
        ) {
            _uiState.update {
                it.copy(errorMessage = "Tous les champs principaux sont obligatoires.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = createPostUseCase.execute(
                question = state.question.trim(),
                leftImageUrl = state.leftImageUrl.trim(),
                rightImageUrl = state.rightImageUrl.trim(),
                leftLabel = state.leftLabel.trim(),
                rightLabel = state.rightLabel.trim(),
                category = state.category.trim()
            )

            if (result.isSuccess) {
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        isPostCreated = true,
                        errorMessage = null
                    )
                }
            } else {
                val error = result.exceptionOrNull()
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        isPostCreated = false,
                        errorMessage = error?.message ?: "Erreur lors de la création du post."
                    )
                }
            }
        }
    }

    fun consumePostCreatedFlag() {
        _uiState.update { it.copy(isPostCreated = false) }
    }
}