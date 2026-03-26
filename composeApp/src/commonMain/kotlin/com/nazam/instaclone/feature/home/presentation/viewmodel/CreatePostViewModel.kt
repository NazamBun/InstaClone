package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.core.access.CreatePostAccess
import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.home.domain.model.UploadProgress
import com.nazam.instaclone.feature.home.domain.model.percentOrNull
import com.nazam.instaclone.feature.home.domain.usecase.CreatePostUseCase
import com.nazam.instaclone.feature.home.domain.usecase.UploadPostImageUseCase
import com.nazam.instaclone.feature.home.presentation.draft.CreatePostDraftStore
import com.nazam.instaclone.feature.home.presentation.model.CreatePostUiState
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.create_post_auth_required
import instaclone.composeapp.generated.resources.create_post_error_create_failed
import instaclone.composeapp.generated.resources.create_post_error_left_label
import instaclone.composeapp.generated.resources.create_post_error_pick_left_image
import instaclone.composeapp.generated.resources.create_post_error_pick_right_image
import instaclone.composeapp.generated.resources.create_post_error_question
import instaclone.composeapp.generated.resources.create_post_error_right_label
import instaclone.composeapp.generated.resources.create_post_error_submit_blocked
import instaclone.composeapp.generated.resources.create_post_error_wait_left_upload
import instaclone.composeapp.generated.resources.create_post_error_wait_right_upload
import instaclone.composeapp.generated.resources.create_post_not_allowed
import instaclone.composeapp.generated.resources.create_post_upload_in_progress
import instaclone.composeapp.generated.resources.create_post_wait
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
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
            leftLocalUri = draft.leftLocalUri,
            rightLocalUri = draft.rightLocalUri,
            leftUploadedUrl = draft.leftUploadedUrl,
            rightUploadedUrl = draft.rightUploadedUrl,
            selectedVsBadgeId = draft.selectedVsBadgeId
        )
    }

    private fun saveToDraft(state: CreatePostUiState) {
        CreatePostDraftStore.update(
            CreatePostDraftStore.Draft(
                question = state.question,
                leftLabel = state.leftLabel,
                rightLabel = state.rightLabel,
                leftLocalUri = state.leftLocalUri,
                rightLocalUri = state.rightLocalUri,
                leftUploadedUrl = state.leftUploadedUrl,
                rightUploadedUrl = state.rightUploadedUrl,
                selectedVsBadgeId = state.selectedVsBadgeId
            )
        )
    }

    private fun updateState(block: (CreatePostUiState) -> CreatePostUiState) {
        _uiState.update { current ->
            val updated = enrich(block(current))
            saveToDraft(updated)
            updated
        }
    }

    private fun enrich(state: CreatePostUiState): CreatePostUiState {
        val reason = when {
            state.isLoading -> UiText.Resource(Res.string.create_post_wait)
            state.isUploadingLeft || state.isUploadingRight ->
                UiText.Resource(Res.string.create_post_upload_in_progress)
            state.question.isBlank() -> UiText.Resource(Res.string.create_post_error_question)
            state.leftLabel.isBlank() -> UiText.Resource(Res.string.create_post_error_left_label)
            state.rightLabel.isBlank() -> UiText.Resource(Res.string.create_post_error_right_label)
            state.leftLocalUri.isBlank() -> UiText.Resource(Res.string.create_post_error_pick_left_image)
            state.rightLocalUri.isBlank() -> UiText.Resource(Res.string.create_post_error_pick_right_image)
            state.leftUploadedUrl.isBlank() -> UiText.Resource(Res.string.create_post_error_wait_left_upload)
            state.rightUploadedUrl.isBlank() -> UiText.Resource(Res.string.create_post_error_wait_right_upload)
            else -> null
        }

        return state.copy(
            isSubmitEnabled = reason == null && !state.isLoading,
            submitBlockedReason = reason
        )
    }

    fun checkAccess() {
        scope.launch {
            val user = withContext(dispatchers.default) { getCurrentUserUseCase.execute() }
            when {
                user == null -> {
                    NavigationStore.setAfterLogin(Screen.CreatePost)
                    _events.tryEmit(CreatePostUiEvent.ShowMessage(UiText.Resource(Res.string.create_post_auth_required)))
                    _events.tryEmit(CreatePostUiEvent.NavigateToLogin)
                }
                !CreatePostAccess.canCreate(user) -> {
                    _events.tryEmit(CreatePostUiEvent.ShowMessage(UiText.Resource(Res.string.create_post_not_allowed)))
                    _events.tryEmit(CreatePostUiEvent.NavigateBack)
                }
            }
        }
    }

    fun refreshFromDraft() {
        _uiState.value = enrich(loadFromDraft())
    }

    fun onQuestionChange(value: String) = updateState { it.copy(question = value, error = null) }
    fun onLeftLabelChange(value: String) = updateState { it.copy(leftLabel = value, error = null) }
    fun onRightLabelChange(value: String) = updateState { it.copy(rightLabel = value, error = null) }
    fun onVsBadgeSelected(value: String) = updateState { it.copy(selectedVsBadgeId = value, error = null) }

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
                leftUploadPercent = 0,
                error = null
            )
        }
        uploadImage(uri, true)
    }

    fun onRightImageSelected(uri: String) {
        updateState {
            it.copy(
                rightLocalUri = uri,
                rightUploadedUrl = "",
                isUploadingRight = true,
                rightUploadPercent = 0,
                error = null
            )
        }
        uploadImage(uri, false)
    }

    private fun uploadImage(uri: String, isLeft: Boolean) {
        scope.launch {
            uploadPostImageUseCase.execute(uri).collect { progress ->
                when (progress) {
                    is UploadProgress.Reading -> updateProgress(isLeft, 0)
                    is UploadProgress.Uploading -> updateProgress(isLeft, progress.percentOrNull())
                    is UploadProgress.Success -> updateSuccess(isLeft, progress.publicUrl)
                    is UploadProgress.Error -> updateError(isLeft, progress.message)
                }
            }
        }
    }

    private fun updateProgress(isLeft: Boolean, percent: Int?) {
        updateState { if (isLeft) it.copy(leftUploadPercent = percent) else it.copy(rightUploadPercent = percent) }
    }

    private fun updateSuccess(isLeft: Boolean, url: String) {
        updateState {
            if (isLeft) it.copy(leftUploadedUrl = url, isUploadingLeft = false, leftUploadPercent = 100)
            else it.copy(rightUploadedUrl = url, isUploadingRight = false, rightUploadPercent = 100)
        }
    }

    private fun updateError(isLeft: Boolean, message: String) {
        updateState {
            val error = UiText.DynamicString(
                message.ifBlank { if (isLeft) "Upload image gauche impossible." else "Upload image droite impossible." }
            )
            if (isLeft) it.copy(isUploadingLeft = false, leftUploadPercent = null, error = error)
            else it.copy(isUploadingRight = false, rightUploadPercent = null, error = error)
        }
    }

    fun submitPost() {
        val state = _uiState.value
        if (!state.isSubmitEnabled) {
            updateState {
                it.copy(error = state.submitBlockedReason ?: UiText.Resource(Res.string.create_post_error_submit_blocked))
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
                    category = "",
                    selectedVsBadgeId = state.selectedVsBadgeId
                )
            }

            result.onSuccess {
                updateState { it.copy(isLoading = false) }
                CreatePostDraftStore.clear()
                _events.tryEmit(CreatePostUiEvent.PostCreated)
            }.onFailure {
                updateState {
                    it.copy(isLoading = false, error = UiText.Resource(Res.string.create_post_error_create_failed))
                }
            }
        }
    }

    fun clear() = job.cancel()
}
