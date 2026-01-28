package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.home.domain.usecase.CreatePostUseCase
import com.nazam.instaclone.feature.home.domain.usecase.UploadPostImageUseCase
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
    private val uploadPostImageUseCase: UploadPostImageUseCase,
    private val createPostUseCase: CreatePostUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) {
    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _uiState = MutableStateFlow(enrich(loadFromDraft()))
    val uiState: StateFlow<CreatePostUiState> = _uiState

    private val _events = MutableSharedFlow<CreatePostUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<CreatePostUiEvent> = _events

    private fun loadFromDraft(): CreatePostUiState {
        val draft = CreatePostDraftStore.get()
        return CreatePostUiState(
            question = draft.question,
            leftLabel = draft.leftLabel,
            rightLabel = draft.rightLabel,
            category = draft.categoryId,
            leftLocalUri = draft.leftLocalUri,
            rightLocalUri = draft.rightLocalUri,
            leftUploadedUrl = draft.leftUploadedUrl,
            rightUploadedUrl = draft.rightUploadedUrl
        )
    }

    private fun saveToDraft(state: CreatePostUiState) {
        CreatePostDraftStore.update(
            CreatePostDraftStore.Draft(
                question = state.question,
                leftLabel = state.leftLabel,
                rightLabel = state.rightLabel,
                categoryId = state.category,
                leftLocalUri = state.leftLocalUri,
                rightLocalUri = state.rightLocalUri,
                leftUploadedUrl = state.leftUploadedUrl,
                rightUploadedUrl = state.rightUploadedUrl
            )
        )
    }

    /**
     * ✅ Etape 2 : une seule porte d’entrée pour modifier l’état
     * - met à jour l’état
     * - recalcule les champs dérivés (submit)
     * - sauvegarde le draft
     */
    private fun updateState(reducer: (CreatePostUiState) -> CreatePostUiState) {
        _uiState.update { current ->
            val updated = reducer(current)
            val enriched = enrich(updated)
            saveToDraft(enriched)
            enriched
        }
    }

    /**
     * Recalcule isSubmitEnabled + submitBlockedReason.
     * UI = bête, ViewModel = intelligent.
     */
    private fun enrich(state: CreatePostUiState): CreatePostUiState {
        val reason = computeSubmitBlockedReason(state)
        val enabled = reason == null && !state.isLoading
        return state.copy(
            isSubmitEnabled = enabled,
            submitBlockedReason = reason
        )
    }

    private fun computeSubmitBlockedReason(state: CreatePostUiState): String? {
        if (state.isLoading) return "Patiente..."
        if (state.isUploadingLeft || state.isUploadingRight) return "Upload des images en cours..."

        if (state.question.isBlank()) return "Écris une question."
        if (state.leftLabel.isBlank()) return "Écris le label gauche."
        if (state.rightLabel.isBlank()) return "Écris le label droite."
        if (state.category.isBlank()) return "Choisis une catégorie."

        if (state.leftLocalUri.isBlank()) return "Choisis l’image gauche."
        if (state.rightLocalUri.isBlank()) return "Choisis l’image droite."

        // Important : on exige les URL uploadées
        if (state.leftUploadedUrl.isBlank()) return "Attends l’upload de l’image gauche."
        if (state.rightUploadedUrl.isBlank()) return "Attends l’upload de l’image droite."

        return null
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
        updateState { it.copy(question = value, errorMessage = null) }
    }

    fun onLeftLabelChange(value: String) {
        updateState { it.copy(leftLabel = value, errorMessage = null) }
    }

    fun onRightLabelChange(value: String) {
        updateState { it.copy(rightLabel = value, errorMessage = null) }
    }

    fun onChooseCategoryClicked() {
        _events.tryEmit(CreatePostUiEvent.NavigateToCategories)
    }

    fun refreshFromDraft() {
        _uiState.value = enrich(loadFromDraft())
    }

    fun onCancelClicked() {
        CreatePostDraftStore.clear()
        _events.tryEmit(CreatePostUiEvent.NavigateBack)
    }

    // ✅ sélection gauche -> upload direct
    fun onLeftImageSelected(uri: String) {
        updateState {
            it.copy(
                leftLocalUri = uri,
                leftUploadedUrl = "",
                isUploadingLeft = true,
                errorMessage = null
            )
        }

        scope.launch {
            val result = withContext(dispatchers.default) {
                uploadPostImageUseCase.execute(localUri = uri)
            }

            result
                .onSuccess { url ->
                    updateState {
                        it.copy(
                            leftUploadedUrl = url,
                            isUploadingLeft = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    if (error is IllegalStateException && error.message == "AUTH_REQUIRED") {
                        updateState { it.copy(isUploadingLeft = false) }
                        NavigationStore.setAfterLogin(Screen.CreatePost)
                        _events.tryEmit(CreatePostUiEvent.ShowMessage("Tu dois être connecté pour créer un post."))
                        _events.tryEmit(CreatePostUiEvent.NavigateToLogin)
                        return@onFailure
                    }

                    updateState {
                        it.copy(
                            isUploadingLeft = false,
                            errorMessage = error.message ?: "Upload image gauche impossible."
                        )
                    }
                }
        }
    }

    // ✅ sélection droite -> upload direct
    fun onRightImageSelected(uri: String) {
        updateState {
            it.copy(
                rightLocalUri = uri,
                rightUploadedUrl = "",
                isUploadingRight = true,
                errorMessage = null
            )
        }

        scope.launch {
            val result = withContext(dispatchers.default) {
                uploadPostImageUseCase.execute(localUri = uri)
            }

            result
                .onSuccess { url ->
                    updateState {
                        it.copy(
                            rightUploadedUrl = url,
                            isUploadingRight = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    if (error is IllegalStateException && error.message == "AUTH_REQUIRED") {
                        updateState { it.copy(isUploadingRight = false) }
                        NavigationStore.setAfterLogin(Screen.CreatePost)
                        _events.tryEmit(CreatePostUiEvent.ShowMessage("Tu dois être connecté pour créer un post."))
                        _events.tryEmit(CreatePostUiEvent.NavigateToLogin)
                        return@onFailure
                    }

                    updateState {
                        it.copy(
                            isUploadingRight = false,
                            errorMessage = error.message ?: "Upload image droite impossible."
                        )
                    }
                }
        }
    }

    fun submitPost() {
        val state = _uiState.value

        // ✅ Ici on s'appuie sur la règle unique
        if (!state.isSubmitEnabled) {
            updateState { it.copy(errorMessage = state.submitBlockedReason ?: "Impossible de publier.") }
            return
        }

        scope.launch {
            updateState { it.copy(isLoading = true, errorMessage = null) }

            val result = withContext(dispatchers.default) {
                createPostUseCase.execute(
                    question = state.question.trim(),
                    leftImageUrl = state.leftUploadedUrl,
                    rightImageUrl = state.rightUploadedUrl,
                    leftLabel = state.leftLabel.trim(),
                    rightLabel = state.rightLabel.trim(),
                    category = state.category.trim()
                )
            }

            result
                .onSuccess {
                    updateState { it.copy(isLoading = false) }
                    CreatePostDraftStore.clear()
                    _events.tryEmit(CreatePostUiEvent.PostCreated)
                }
                .onFailure { error ->
                    if (error is IllegalStateException && error.message == "AUTH_REQUIRED") {
                        updateState { it.copy(isLoading = false) }
                        NavigationStore.setAfterLogin(Screen.CreatePost)
                        _events.tryEmit(CreatePostUiEvent.ShowMessage("Tu dois être connecté pour créer un post."))
                        _events.tryEmit(CreatePostUiEvent.NavigateToLogin)
                        return@onFailure
                    }

                    updateState {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erreur lors de la création du post."
                        )
                    }
                }
        }
    }

    fun clear() {
        job.cancel()
    }
}