package com.nazam.instaclone.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nazam.instaclone.core.access.CreatePostAccess
import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.home.domain.model.UploadProgress
import com.nazam.instaclone.feature.home.domain.model.percentOrNull
import com.nazam.instaclone.feature.home.domain.usecase.CreateYesNoPostUseCase
import com.nazam.instaclone.feature.home.domain.usecase.UploadPostImageUseCase
import com.nazam.instaclone.feature.home.presentation.model.CreatePostYesNoUiState
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.create_post_auth_required
import instaclone.composeapp.generated.resources.create_post_error_create_failed
import instaclone.composeapp.generated.resources.create_post_error_question
import instaclone.composeapp.generated.resources.create_post_not_allowed
import instaclone.composeapp.generated.resources.create_post_upload_in_progress
import instaclone.composeapp.generated.resources.create_post_wait
import instaclone.composeapp.generated.resources.create_post_yes_no_error_image
import instaclone.composeapp.generated.resources.create_post_yes_no_error_upload_failed
import instaclone.composeapp.generated.resources.create_post_yes_no_error_wait_upload
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreatePostYesNoViewModel(
    private val dispatchers: AppDispatchers,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val uploadPostImageUseCase: UploadPostImageUseCase,
    private val createYesNoPostUseCase: CreateYesNoPostUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(enrich(CreatePostYesNoUiState()))
    val uiState: StateFlow<CreatePostYesNoUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CreatePostYesNoUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<CreatePostYesNoUiEvent> = _events.asSharedFlow()

    fun checkAccess() {
        viewModelScope.launch {
            val user = withContext(dispatchers.io) { getCurrentUserUseCase.execute() }
            when {
                user == null -> {
                    NavigationStore.setAfterLogin(Screen.CreatePostYesNo)
                    updateState { it.copy(authBlocked = true) }
                    _events.tryEmit(CreatePostYesNoUiEvent.ShowMessage(UiText.Resource(Res.string.create_post_auth_required)))
                    _events.tryEmit(CreatePostYesNoUiEvent.NavigateToLogin)
                }
                !CreatePostAccess.canCreate(user) -> {
                    updateState { it.copy(authBlocked = true) }
                    _events.tryEmit(CreatePostYesNoUiEvent.ShowMessage(UiText.Resource(Res.string.create_post_not_allowed)))
                    _events.tryEmit(CreatePostYesNoUiEvent.NavigateBack)
                }
            }
        }
    }

    fun onQuestionChange(value: String) = updateState { it.copy(question = value, error = null) }

    fun onImageSelected(uri: String) {
        updateState {
            it.copy(
                imageLocalUri = uri, imageUploadedUrl = "",
                isUploadingImage = true, imageUploadPercent = 0, error = null
            )
        }
        uploadImage(uri)
    }

    fun submitPost() {
        val state = _uiState.value
        if (state.submitBlockedReason != null) {
            updateState { it.copy(error = state.submitBlockedReason) }
            return
        }
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null) }
            val result = withContext(dispatchers.io) {
                createYesNoPostUseCase.execute(question = state.question.trim(), imageUrl = state.imageUploadedUrl)
            }
            result.onSuccess {
                updateState { it.copy(isLoading = false) }
                _events.tryEmit(CreatePostYesNoUiEvent.PostCreated)
            }.onFailure {
                updateState {
                    it.copy(isLoading = false, error = UiText.Resource(Res.string.create_post_error_create_failed))
                }
            }
        }
    }

    fun onCancelClicked() { _events.tryEmit(CreatePostYesNoUiEvent.NavigateBack) }

    fun dismissError() = updateState { it.copy(error = null) }

    private fun uploadImage(uri: String) {
        viewModelScope.launch {
            uploadPostImageUseCase.execute(uri).collect { progress ->
                when (progress) {
                    is UploadProgress.Reading -> updateState { it.copy(imageUploadPercent = 0) }
                    is UploadProgress.Uploading -> updateState { it.copy(imageUploadPercent = progress.percentOrNull()) }
                    is UploadProgress.Success -> updateState {
                        it.copy(imageUploadedUrl = progress.publicUrl, isUploadingImage = false, imageUploadPercent = 100)
                    }
                    is UploadProgress.Error -> updateState {
                        it.copy(
                            isUploadingImage = false, imageUploadPercent = null,
                            error = UiText.Resource(Res.string.create_post_yes_no_error_upload_failed)
                        )
                    }
                }
            }
        }
    }

    private fun updateState(block: (CreatePostYesNoUiState) -> CreatePostYesNoUiState) {
        _uiState.update { current -> enrich(block(current)) }
    }

    private fun enrich(state: CreatePostYesNoUiState): CreatePostYesNoUiState =
        state.copy(submitBlockedReason = computeBlockedReason(state))

    private fun computeBlockedReason(state: CreatePostYesNoUiState): UiText? = when {
        state.isLoading -> UiText.Resource(Res.string.create_post_wait)
        state.isUploadingImage -> UiText.Resource(Res.string.create_post_upload_in_progress)
        state.question.isBlank() -> UiText.Resource(Res.string.create_post_error_question)
        state.imageLocalUri.isBlank() -> UiText.Resource(Res.string.create_post_yes_no_error_image)
        state.imageUploadedUrl.isBlank() -> UiText.Resource(Res.string.create_post_yes_no_error_wait_upload)
        else -> null
    }
}
