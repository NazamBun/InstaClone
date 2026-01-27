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

    fun onChooseCategoryClicked() {
        _events.tryEmit(CreatePostUiEvent.NavigateToCategories)
    }

    fun refreshFromDraft() {
        _uiState.value = loadFromDraft()
    }

    fun onCancelClicked() {
        CreatePostDraftStore.clear()
        _events.tryEmit(CreatePostUiEvent.NavigateBack)
    }

    // ✅ 1) sélection gauche -> upload direct
    fun onLeftImageSelected(uri: String) {
        _uiState.update {
            it.copy(
                leftLocalUri = uri,
                leftUploadedUrl = "",
                isUploadingLeft = true,
                errorMessage = null
            )
        }
        saveToDraft(_uiState.value)

        scope.launch {
            val result = withContext(dispatchers.default) {
                uploadPostImageUseCase.execute(localUri = uri)
            }

            result
                .onSuccess { url ->
                    _uiState.update {
                        it.copy(
                            leftUploadedUrl = url,
                            isUploadingLeft = false
                        )
                    }
                    saveToDraft(_uiState.value)
                }
                .onFailure { error ->
                    if (error is IllegalStateException && error.message == "AUTH_REQUIRED") {
                        _uiState.update { it.copy(isUploadingLeft = false) }
                        NavigationStore.setAfterLogin(Screen.CreatePost)
                        _events.tryEmit(CreatePostUiEvent.ShowMessage("Tu dois être connecté pour créer un post."))
                        _events.tryEmit(CreatePostUiEvent.NavigateToLogin)
                        return@onFailure
                    }

                    _uiState.update {
                        it.copy(
                            isUploadingLeft = false,
                            errorMessage = error.message ?: "Upload image gauche impossible."
                        )
                    }
                }
        }
    }

    // ✅ 1) sélection droite -> upload direct
    fun onRightImageSelected(uri: String) {
        _uiState.update {
            it.copy(
                rightLocalUri = uri,
                rightUploadedUrl = "",
                isUploadingRight = true,
                errorMessage = null
            )
        }
        saveToDraft(_uiState.value)

        scope.launch {
            val result = withContext(dispatchers.default) {
                uploadPostImageUseCase.execute(localUri = uri)
            }

            result
                .onSuccess { url ->
                    _uiState.update {
                        it.copy(
                            rightUploadedUrl = url,
                            isUploadingRight = false
                        )
                    }
                    saveToDraft(_uiState.value)
                }
                .onFailure { error ->
                    if (error is IllegalStateException && error.message == "AUTH_REQUIRED") {
                        _uiState.update { it.copy(isUploadingRight = false) }
                        NavigationStore.setAfterLogin(Screen.CreatePost)
                        _events.tryEmit(CreatePostUiEvent.ShowMessage("Tu dois être connecté pour créer un post."))
                        _events.tryEmit(CreatePostUiEvent.NavigateToLogin)
                        return@onFailure
                    }

                    _uiState.update {
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

        // ✅ On exige les URLs uploadées (pas juste les URIs locales)
        val missing =
            state.question.isBlank() ||
                    state.leftLabel.isBlank() ||
                    state.rightLabel.isBlank() ||
                    state.category.isBlank() ||
                    state.leftLocalUri.isBlank() ||
                    state.rightLocalUri.isBlank() ||
                    state.leftUploadedUrl.isBlank() ||
                    state.rightUploadedUrl.isBlank()

        val uploading = state.isUploadingLeft || state.isUploadingRight

        if (missing) {
            _uiState.update { it.copy(errorMessage = "Tous les champs sont obligatoires (images incluses).") }
            return
        }

        if (uploading) {
            _uiState.update { it.copy(errorMessage = "Attends la fin de l’upload des images.") }
            return
        }

        scope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

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

                    _uiState.update {
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