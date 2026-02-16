package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.home.domain.usecase.CreatePostUseCase
import com.nazam.instaclone.feature.home.domain.usecase.UploadPostImageUseCase
import com.nazam.instaclone.feature.home.presentation.draft.CreatePostDraftStore
import com.nazam.instaclone.feature.home.presentation.model.CreatePostUiState
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.create_post_auth_required
import instaclone.composeapp.generated.resources.create_post_error_category
import instaclone.composeapp.generated.resources.create_post_error_create_failed
import instaclone.composeapp.generated.resources.create_post_error_left_label
import instaclone.composeapp.generated.resources.create_post_error_pick_left_image
import instaclone.composeapp.generated.resources.create_post_error_pick_right_image
import instaclone.composeapp.generated.resources.create_post_error_question
import instaclone.composeapp.generated.resources.create_post_error_right_label
import instaclone.composeapp.generated.resources.create_post_error_submit_blocked
import instaclone.composeapp.generated.resources.create_post_error_upload_left_failed
import instaclone.composeapp.generated.resources.create_post_error_upload_right_failed
import instaclone.composeapp.generated.resources.create_post_error_wait_left_upload
import instaclone.composeapp.generated.resources.create_post_error_wait_right_upload
import instaclone.composeapp.generated.resources.create_post_upload_in_progress
import instaclone.composeapp.generated.resources.create_post_wait
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel KMP pur.
 * - StateFlow = état durable
 * - SharedFlow = events one-shot (navigation, snackbars)
 */
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
     * Une seule porte d’entrée :
     * - met à jour l’état
     * - recalcule isSubmitEnabled + submitBlockedReason
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

    private fun enrich(state: CreatePostUiState): CreatePostUiState {
        val reason = computeSubmitBlockedReason(state)
        val enabled = reason == null && !state.isLoading
        return state.copy(
            isSubmitEnabled = enabled,
            submitBlockedReason = reason
        )
    }

    private fun computeSubmitBlockedReason(state: CreatePostUiState): UiText? {
        return when {
            state.isLoading -> UiText.Resource(Res.string.create_post_wait)
            state.isUploadingLeft || state.isUploadingRight ->
                UiText.Resource(Res.string.create_post_upload_in_progress)

            state.question.isBlank() -> UiText.Resource(Res.string.create_post_error_question)
            state.leftLabel.isBlank() -> UiText.Resource(Res.string.create_post_error_left_label)
            state.rightLabel.isBlank() -> UiText.Resource(Res.string.create_post_error_right_label)
            state.category.isBlank() -> UiText.Resource(Res.string.create_post_error_category)

            state.leftLocalUri.isBlank() -> UiText.Resource(Res.string.create_post_error_pick_left_image)
            state.rightLocalUri.isBlank() -> UiText.Resource(Res.string.create_post_error_pick_right_image)

            // On exige les URLs uploadées
            state.leftUploadedUrl.isBlank() -> UiText.Resource(Res.string.create_post_error_wait_left_upload)
            state.rightUploadedUrl.isBlank() -> UiText.Resource(Res.string.create_post_error_wait_right_upload)

            else -> null
        }
    }

    fun checkAccess() {
        scope.launch {
            val user = withContext(dispatchers.default) { getCurrentUserUseCase.execute() }
            if (user == null) {
                NavigationStore.setAfterLogin(Screen.CreatePost)
                _events.tryEmit(
                    CreatePostUiEvent.ShowMessage(UiText.Resource(Res.string.create_post_auth_required))
                )
                _events.tryEmit(CreatePostUiEvent.NavigateToLogin)
            }
        }
    }

    fun refreshFromDraft() {
        _uiState.value = enrich(loadFromDraft())
    }

    fun onQuestionChange(value: String) {
        updateState { it.copy(question = value, error = null) }
    }

    fun onLeftLabelChange(value: String) {
        updateState { it.copy(leftLabel = value, error = null) }
    }

    fun onRightLabelChange(value: String) {
        updateState { it.copy(rightLabel = value, error = null) }
    }

    fun onChooseCategoryClicked() {
        _events.tryEmit(CreatePostUiEvent.NavigateToCategories)
    }

    fun onCancelClicked() {
        CreatePostDraftStore.clear()
        _events.tryEmit(CreatePostUiEvent.NavigateBack)
    }

    fun onLeftImageSelected(uri: String) {
        updateState {
            it.copy(
                leftLocalUri = uri,
                leftUploadedUrl = "",
                isUploadingLeft = true,
                error = null
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
                            error = null
                        )
                    }
                }
                .onFailure { throwable ->
                    if (throwable is IllegalStateException && throwable.message == "AUTH_REQUIRED") {
                        updateState { it.copy(isUploadingLeft = false) }
                        NavigationStore.setAfterLogin(Screen.CreatePost)
                        _events.tryEmit(
                            CreatePostUiEvent.ShowMessage(UiText.Resource(Res.string.create_post_auth_required))
                        )
                        _events.tryEmit(CreatePostUiEvent.NavigateToLogin)
                        return@onFailure
                    }

                    val msg = throwable.message?.takeIf { it.isNotBlank() }
                    updateState {
                        it.copy(
                            isUploadingLeft = false,
                            error = msg?.let { UiText.DynamicString(it) }
                                ?: UiText.Resource(Res.string.create_post_error_upload_left_failed)
                        )
                    }
                }
        }
    }

    fun onRightImageSelected(uri: String) {
        updateState {
            it.copy(
                rightLocalUri = uri,
                rightUploadedUrl = "",
                isUploadingRight = true,
                error = null
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
                            error = null
                        )
                    }
                }
                .onFailure { throwable ->
                    if (throwable is IllegalStateException && throwable.message == "AUTH_REQUIRED") {
                        updateState { it.copy(isUploadingRight = false) }
                        NavigationStore.setAfterLogin(Screen.CreatePost)
                        _events.tryEmit(
                            CreatePostUiEvent.ShowMessage(UiText.Resource(Res.string.create_post_auth_required))
                        )
                        _events.tryEmit(CreatePostUiEvent.NavigateToLogin)
                        return@onFailure
                    }

                    val msg = throwable.message?.takeIf { it.isNotBlank() }
                    updateState {
                        it.copy(
                            isUploadingRight = false,
                            error = msg?.let { UiText.DynamicString(it) }
                                ?: UiText.Resource(Res.string.create_post_error_upload_right_failed)
                        )
                    }
                }
        }
    }

    fun submitPost() {
        val state = _uiState.value

        // Une seule règle : si c’est bloqué, on affiche la raison
        if (!state.isSubmitEnabled) {
            updateState {
                it.copy(
                    error = state.submitBlockedReason
                        ?: UiText.Resource(Res.string.create_post_error_submit_blocked)
                )
            }
            return
        }

        scope.launch {
            updateState { it.copy(isLoading = true, error = null) }

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
                .onFailure { throwable ->
                    if (throwable is IllegalStateException && throwable.message == "AUTH_REQUIRED") {
                        updateState { it.copy(isLoading = false) }
                        NavigationStore.setAfterLogin(Screen.CreatePost)
                        _events.tryEmit(
                            CreatePostUiEvent.ShowMessage(UiText.Resource(Res.string.create_post_auth_required))
                        )
                        _events.tryEmit(CreatePostUiEvent.NavigateToLogin)
                        return@onFailure
                    }

                    val msg = throwable.message?.takeIf { it.isNotBlank() }
                    updateState {
                        it.copy(
                            isLoading = false,
                            error = msg?.let { UiText.DynamicString(it) }
                                ?: UiText.Resource(Res.string.create_post_error_create_failed)
                        )
                    }
                }
        }
    }

    fun clear() {
        job.cancel()
    }
}