package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.feature.home.domain.usecase.CreatePostUseCase
import com.nazam.instaclone.feature.home.presentation.model.CreatePostUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ✅ ViewModel KMP "pur"
 * - Pas de KoinComponent ici
 * - Dépendances via constructeur (SOLID)
 */
class CreatePostViewModel(
    private val dispatchers: AppDispatchers,
    private val createPostUseCase: CreatePostUseCase
) {

    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState

    /** ✅ Quand la question change */
    fun onQuestionChange(value: String) {
        _uiState.update { it.copy(question = value, errorMessage = null) }
    }

    /** ✅ Quand le label gauche change */
    fun onLeftLabelChange(value: String) {
        _uiState.update { it.copy(leftLabel = value, errorMessage = null) }
    }

    /** ✅ Quand le label droit change */
    fun onRightLabelChange(value: String) {
        _uiState.update { it.copy(rightLabel = value, errorMessage = null) }
    }

    /** ✅ Quand l'url gauche change */
    fun onLeftImageUrlChange(value: String) {
        _uiState.update { it.copy(leftImageUrl = value, errorMessage = null) }
    }

    /** ✅ Quand l'url droite change */
    fun onRightImageUrlChange(value: String) {
        _uiState.update { it.copy(rightImageUrl = value, errorMessage = null) }
    }

    /** ✅ Quand la catégorie change */
    fun onCategoryChange(value: String) {
        _uiState.update { it.copy(category = value, errorMessage = null) }
    }

    /** ✅ Clique sur "Créer" */
    fun submitPost() {
        val state = _uiState.value

        // ✅ mini validation UI (simple)
        if (
            state.question.isBlank() ||
            state.leftLabel.isBlank() ||
            state.rightLabel.isBlank() ||
            state.leftImageUrl.isBlank() ||
            state.rightImageUrl.isBlank()
        ) {
            _uiState.update { it.copy(errorMessage = "Tous les champs principaux sont obligatoires.") }
            return
        }

        scope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = withContext(dispatchers.default) {
                createPostUseCase.execute(
                    question = state.question.trim(),
                    leftImageUrl = state.leftImageUrl.trim(),
                    rightImageUrl = state.rightImageUrl.trim(),
                    leftLabel = state.leftLabel.trim(),
                    rightLabel = state.rightLabel.trim(),
                    category = state.category.trim()
                )
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isPostCreated = true) }
                }
                .onFailure { error ->
                    val msg =
                        if (error is IllegalStateException && error.message == "AUTH_REQUIRED") {
                            "Tu dois être connecté pour créer un post"
                        } else {
                            error.message ?: "Erreur lors de la création du post."
                        }

                    _uiState.update { it.copy(isLoading = false, isPostCreated = false, errorMessage = msg) }
                }
        }
    }

    /** ✅ Réinitialise le flag (après navigation) */
    fun consumePostCreatedFlag() {
        _uiState.update { it.copy(isPostCreated = false) }
    }

    /** ✅ Stoppe les coroutines */
    fun clear() {
        job.cancel()
    }
}