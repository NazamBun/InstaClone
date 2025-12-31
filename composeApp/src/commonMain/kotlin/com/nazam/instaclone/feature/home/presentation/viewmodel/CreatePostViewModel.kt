package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.home.domain.usecase.CreatePostUseCase
import com.nazam.instaclone.feature.home.presentation.draft.CreatePostDraftStore
import com.nazam.instaclone.feature.home.presentation.model.CreatePostUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreatePostViewModel(
    private val dispatchers: AppDispatchers,
    private val createPostUseCase: CreatePostUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) {
    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _uiState = MutableStateFlow(loadFromDraft())
    val uiState: StateFlow<CreatePostUiState> = _uiState

    private val _events = MutableSharedFlow<CreatePostUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<CreatePostUiEvent> = _events

    private fun loadFromDraft(): CreatePostUiState {
        val draft = CreatePostDraftStore.get()
        return CreatePostUiState(
            question = draft.question,
            leftLabel = draft.leftLabel,
            rightLabel = draft.rightLabel,
            leftImageUrl = draft.leftImageUrl,
            rightImageUrl = draft.rightImageUrl,
            category = draft.categoryId
        )
    }

    private fun saveToDraft(state: CreatePostUiState) {
        CreatePostDraftStore.update(
            CreatePostDraftStore.Draft(
                question = state.question,
                leftLabel = state.leftLabel,
                rightLabel = state.rightLabel,
                leftImageUrl = state.leftImageUrl,
                rightImageUrl = state.rightImageUrl,
                categoryId = state.category
            )
        )
    }

    fun checkAccess() {
        scope.launch {
            val user = withContext(dispatchers.default) { getCurrentUserUseCase.execute() }
            if (user == null) {
                NavigationStore.setAfterLogin(Screen.CreatePost)
                _events.tryEmit(CreatePostUiEvent.ShowMessage("Tu dois être connecté pour créer un post."))
                _events.tryEmit(CreatePostUiEvent.NavigateToLogin)
            }
        }
    }

    fun onQuestionChange(value: String) {
        _uiState.update { it.copy(question = value, errorMessage = null) }
        saveToDraft(_uiState.value)
    }

    fun onLeftLabelChange(value: String) {
        _uiState.update { it.copy(leftLabel = value, errorMessage = null) }
        saveToDraft(_uiState.value)
    }

    fun onRightLabelChange(value: String) {
        _uiState.update { it.copy(rightLabel = value, errorMessage = null) }
        saveToDraft(_uiState.value)
    }

    fun onLeftImageUrlChange(value: String) {
        _uiState.update { it.copy(leftImageUrl = value, errorMessage = null) }
        saveToDraft(_uiState.value)
    }

    fun onRightImageUrlChange(value: String) {
        _uiState.update { it.copy(rightImageUrl = value, errorMessage = null) }
        saveToDraft(_uiState.value)
    }

    /**
     * Ici on ne laisse plus l'utilisateur taper une catégorie librement.
     * C'est CategoriesScreen qui la choisit.
     */
    fun onChooseCategoryClicked() {
        _events.tryEmit(CreatePostUiEvent.NavigateToCategories)
    }

    /**
     * Quand on revient de CategoriesScreen, le draft contient la nouvelle categoryId.
     * On recharge l'UiState.
     */
    fun refreshFromDraft() {
        _uiState.value = loadFromDraft()
    }

    fun onCancelClicked() {
        CreatePostDraftStore.clear()
        _events.tryEmit(CreatePostUiEvent.NavigateBack)
    }

    fun submitPost() {
        val state = _uiState.value

        if (
            state.question.isBlank() ||
            state.leftLabel.isBlank() ||
            state.rightLabel.isBlank() ||
            state.leftImageUrl.isBlank() ||
            state.rightImageUrl.isBlank() ||
            state.category.isBlank()
        ) {
            _uiState.update { it.copy(errorMessage = "Tous les champs sont obligatoires, y compris la catégorie.") }
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
                    _uiState.update { it.copy(isLoading = false) }
                    CreatePostDraftStore.clear()
                    _events.tryEmit(CreatePostUiEvent.PostCreated)
                }
                .onFailure { error ->
                    if (error is IllegalStateException && error.message == "AUTH_REQUIRED") {
                        _uiState.update { it.copy(isLoading = false) }
                        NavigationStore.setAfterLogin(Screen.CreatePost)
                        _events.tryEmit(CreatePostUiEvent.ShowMessage("Tu dois être connecté pour créer un post."))
                        _events.tryEmit(CreatePostUiEvent.NavigateToLogin)
                        return@onFailure
                    }

                    val msg = error.message ?: "Erreur lors de la création du post."
                    _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
                }
        }
    }

    fun clear() {
        job.cancel()
    }
}