package com.nazam.instaclone.feature.profile.presentation.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.ui.SnackbarStore
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.profile.domain.model.UpdateProfile
import com.nazam.instaclone.feature.profile.domain.usecase.GetMyProfileUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.UpdateMyProfileUseCase
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.edit_profile_save_error
import instaclone.composeapp.generated.resources.profile_load_error
import instaclone.composeapp.generated.resources.profile_updated
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileViewModel(
    private val dispatchers: AppDispatchers,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val updateMyProfileUseCase: UpdateMyProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState(isLoading = true))
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<Screen>(extraBufferCapacity = 1)
    val events: SharedFlow<Screen> = _events.asSharedFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val user = withContext(dispatchers.io) { getCurrentUserUseCase.execute() }
                ?: return@launch fail(UiText.Resource(Res.string.profile_load_error))

            val profile = withContext(dispatchers.io) {
                getMyProfileUseCase.execute(userId = user.id, emailFallback = user.email)
            }.getOrNull() ?: return@launch fail(UiText.Resource(Res.string.profile_load_error))

            _uiState.update {
                it.copy(
                    isLoading = false,
                    displayName = profile.displayName,
                    username = profile.username,
                    bio = profile.bio,
                    location = profile.location,
                    website = profile.website,
                    error = null
                )
            }
        }
    }

    fun onDisplayNameChange(v: String) = _uiState.update { it.copy(displayName = v) }
    fun onUsernameChange(v: String) = _uiState.update { it.copy(username = v) }
    fun onBioChange(v: String) = _uiState.update { it.copy(bio = v) }
    fun onLocationChange(v: String) = _uiState.update { it.copy(location = v) }
    fun onWebsiteChange(v: String) = _uiState.update { it.copy(website = v) }

    fun save() {
        viewModelScope.launch {
            val user = withContext(dispatchers.io) { getCurrentUserUseCase.execute() } ?: return@launch
            val s = _uiState.value
            _uiState.update { it.copy(isSaving = true, error = null) }

            val update = UpdateProfile(
                displayName = s.displayName,
                username = s.username,
                bio = s.bio,
                location = s.location,
                website = s.website
            )
            withContext(dispatchers.io) { updateMyProfileUseCase.execute(user.id, update) }
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false) }
                    SnackbarStore.show(UiText.Resource(Res.string.profile_updated))
                    _events.tryEmit(Screen.Profile)
                }
                .onFailure {
                    _uiState.update {
                        it.copy(isSaving = false, error = UiText.Resource(Res.string.edit_profile_save_error))
                    }
                }
        }
    }

    private fun fail(error: UiText) {
        _uiState.update { it.copy(isLoading = false, error = error) }
    }
}
