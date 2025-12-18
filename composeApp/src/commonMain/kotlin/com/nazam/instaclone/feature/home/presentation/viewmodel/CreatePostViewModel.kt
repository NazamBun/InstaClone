package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.feature.home.domain.usecase.CreatePostUseCase
import com.nazam.instaclone.feature.home.presentation.model.CreatePostUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CreatePostViewModel : KoinComponent {

    private val createPostUseCase: CreatePostUseCase by inject()

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState

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

    fun submitPost() {
        val state = _uiState.value

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

        scope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = createPostUseCase.execute(
                question = state.question.trim(),
                leftImageUrl = state.leftImageUrl.trim(),
                rightImageUrl = state.rightImageUrl.trim(),
                leftLabel = state.leftLabel.trim(),
                rightLabel = state.rightLabel.trim(),
                category = state.category.trim()
            )

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isPostCreated = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isPostCreated = false,
                            errorMessage = error.message ?: "Erreur lors de la création du post."
                        )
                    }
                }
        }
    }

    fun consumePostCreatedFlag() {
        _uiState.update { it.copy(isPostCreated = false) }
    }

    fun clear() {
        job.cancel()
    }
}