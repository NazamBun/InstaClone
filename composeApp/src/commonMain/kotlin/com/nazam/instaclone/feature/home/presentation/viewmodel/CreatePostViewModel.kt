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
        val d = CreatePostDraftStore.get()
        return CreatePostUiState(
            question = d.question,
            leftLabel = d.leftLabel,
            rightLabel = d.rightLabel,
            category = d.categoryId,
            leftLocalUri = d.leftLocalUri,
            rightLocalUri = d.rightLocalUri,
            leftUploadedUrl = d.leftUploadedUrl,
            rightUploadedUrl = d.rightUploadedUrl
        )
    }

    private fun saveToDraft(s: CreatePostUiState) {
        CreatePostDraftStore.update(
            CreatePostDraftStore.Draft(
                question = s.question,
                leftLabel = s.leftLabel,
                rightLabel = s.rightLabel,
                categoryId = s.category,
                leftLocalUri = s.leftLocalUri,
                rightLocalUri = s.rightLocalUri,
                leftUploadedUrl = s.leftUploadedUrl,
                rightUploadedUrl = s.rightUploadedUrl
            )
        )
    }

    private fun updateState(reducer: (CreatePostUiState) -> CreatePostUiState) {
        _uiState.update { cur ->
            val up = enrich(reducer(cur))
            saveToDraft(up)
            up
        }
    }

    private fun enrich(s: CreatePostUiState): CreatePostUiState {
        val reason = computeSubmitBlockedReason(s)
        return s.copy(
            isSubmitEnabled = reason == null && !s.isLoading,
            submitBlockedReason = reason
        )
    }

    private fun computeSubmitBlockedReason(s: CreatePostUiState): UiText? = when {
        s.isLoading -> UiText.Resource(Res.string.create_post_wait)
        s.isUploadingLeft || s.isUploadingRight ->
            UiText.Resource(Res.string.create_post_upload_in_progress)
        s.question.isBlank() -> UiText.Resource(Res.string.create_post_error_question)
        s.leftLabel.isBlank() -> UiText.Resource(Res.string.create_post_error_left_label)
        s.rightLabel.isBlank() -> UiText.Resource(Res.string.create_post_error_right_label)
        s.category.isBlank() -> UiText.Resource(Res.string.create_post_error_category)
        s.leftLocalUri.isBlank() -> UiText.Resource(Res.string.create_post_error_pick_left_image)
        s.rightLocalUri.isBlank() -> UiText.Resource(Res.string.create_post_error_pick_right_image)
        s.leftUploadedUrl.isBlank() -> UiText.Resource(Res.string.create_post_error_wait_left_upload)
        s.rightUploadedUrl.isBlank() -> UiText.Resource(Res.string.create_post_error_wait_right_upload)
        else -> null
    }

    fun checkAccess() {
        scope.launch {
            val user = withContext(dispatchers.default) { getCurrentUserUseCase.execute() }
            if (user == null) {
                NavigationStore.setAfterLogin(Screen.CreatePost)
                _events.tryEmit(CreatePostUiEvent.ShowMessage(UiText.Resource(Res.string.create_post_auth_required)))
                _events.tryEmit(CreatePostUiEvent.NavigateToLogin)
            } else if (!CreatePostAccess.canCreate(user)) {
                _events.tryEmit(CreatePostUiEvent.ShowMessage(UiText.Resource(Res.string.create_post_not_allowed)))
                _events.tryEmit(CreatePostUiEvent.NavigateBack)
            }
        }
    }

    fun refreshFromDraft() {
        _uiState.value = enrich(loadFromDraft())
    }

    fun onQuestionChange(v: String) = updateState { it.copy(question = v, error = null) }
    fun onLeftLabelChange(v: String) = updateState { it.copy(leftLabel = v, error = null) }
    fun onRightLabelChange(v: String) = updateState { it.copy(rightLabel = v, error = null) }

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
                leftUploadPercent = 0,
                error = null
            )
        }

        scope.launch {
            uploadPostImageUseCase.execute(uri).collect { p ->
                when (p) {
                    is UploadProgress.Reading -> updateState { it.copy(leftUploadPercent = 0) }
                    is UploadProgress.Uploading -> updateState { it.copy(leftUploadPercent = p.percentOrNull()) }
                    is UploadProgress.Success -> updateState {
                        it.copy(
                            leftUploadedUrl = p.publicUrl,
                            isUploadingLeft = false,
                            leftUploadPercent = 100
                        )
                    }
                    is UploadProgress.Error -> updateState {
                        it.copy(
                            isUploadingLeft = false,
                            leftUploadPercent = null,
                            error = UiText.DynamicString(p.message.ifBlank { "Upload image gauche impossible." })
                        )
                    }
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
                rightUploadPercent = 0,
                error = null
            )
        }

        scope.launch {
            uploadPostImageUseCase.execute(uri).collect { p ->
                when (p) {
                    is UploadProgress.Reading -> updateState { it.copy(rightUploadPercent = 0) }
                    is UploadProgress.Uploading -> updateState { it.copy(rightUploadPercent = p.percentOrNull()) }
                    is UploadProgress.Success -> updateState {
                        it.copy(
                            rightUploadedUrl = p.publicUrl,
                            isUploadingRight = false,
                            rightUploadPercent = 100
                        )
                    }
                    is UploadProgress.Error -> updateState {
                        it.copy(
                            isUploadingRight = false,
                            rightUploadPercent = null,
                            error = UiText.DynamicString(p.message.ifBlank { "Upload image droite impossible." })
                        )
                    }
                }
            }
        }
    }

    fun submitPost() {
        val s = _uiState.value
        if (!s.isSubmitEnabled) {
            updateState { it.copy(error = s.submitBlockedReason ?: UiText.Resource(Res.string.create_post_error_submit_blocked)) }
            return
        }

        scope.launch {
            updateState { it.copy(isLoading = true, error = null) }

            val result = withContext(dispatchers.default) {
                createPostUseCase.execute(
                    question = s.question.trim(),
                    leftImageUrl = s.leftUploadedUrl,
                    rightImageUrl = s.rightUploadedUrl,
                    leftLabel = s.leftLabel.trim(),
                    rightLabel = s.rightLabel.trim(),
                    category = s.category.trim()
                )
            }

            result.onSuccess {
                updateState { it.copy(isLoading = false) }
                CreatePostDraftStore.clear()
                _events.tryEmit(CreatePostUiEvent.PostCreated)
            }.onFailure {
                updateState { it.copy(isLoading = false, error = UiText.Resource(Res.string.create_post_error_create_failed)) }
            }
        }
    }

    fun clear() = job.cancel()
}
