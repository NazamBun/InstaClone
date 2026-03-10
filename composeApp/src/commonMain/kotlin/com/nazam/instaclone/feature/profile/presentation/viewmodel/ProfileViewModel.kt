package com.nazam.instaclone.feature.profile.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.session.SessionManager
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LogoutUseCase
import com.nazam.instaclone.feature.home.domain.model.UploadProgress
import com.nazam.instaclone.feature.home.domain.usecase.UploadPostImageUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.GetFollowersCountUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.GetFollowingCountUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.GetMyPostsUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.GetMyProfileUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.UpdateAvatarUseCase
import com.nazam.instaclone.feature.profile.presentation.model.ProfileUiState
import com.nazam.instaclone.feature.profile.presentation.navigation.ProfileTargetStore
import com.nazam.instaclone.feature.profile.presentation.ui.ProfileUi
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.profile_load_error
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    private val dispatchers: AppDispatchers,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val getMyPostsUseCase: GetMyPostsUseCase,
    private val getFollowersCountUseCase: GetFollowersCountUseCase,
    private val getFollowingCountUseCase: GetFollowingCountUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val sessionManager: SessionManager,
    private val uploadPostImageUseCase: UploadPostImageUseCase,
    private val updateAvatarUseCase: UpdateAvatarUseCase
) {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val _events = MutableSharedFlow<ProfileUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProfileUiEvent> = _events

    init {
        load()
    }

    fun load() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val currentUser = withContext(dispatchers.io) { getCurrentUserUseCase.execute() }
            if (currentUser == null) {
                _uiState.update {
                    it.copy(isLoading = false, ui = null, error = UiText.Resource(Res.string.profile_load_error))
                }
                return@launch
            }

            val targetUserId = ProfileTargetStore.getUserId() ?: currentUser.id
            val targetEmail = ProfileTargetStore.getEmailFallback() ?: currentUser.email

            val profileResult = withContext(dispatchers.io) {
                getMyProfileUseCase.execute(userId = targetUserId, emailFallback = targetEmail)
            }
            val postsResult = withContext(dispatchers.io) {
                getMyPostsUseCase.execute(authorId = targetUserId)
            }
            val followersResult = withContext(dispatchers.io) {
                getFollowersCountUseCase.execute(userId = targetUserId)
            }
            val followingResult = withContext(dispatchers.io) {
                getFollowingCountUseCase.execute(userId = targetUserId)
            }

            val profile = profileResult.getOrNull()
            if (profile == null) {
                _uiState.update {
                    it.copy(isLoading = false, ui = null, error = UiText.Resource(Res.string.profile_load_error))
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    ui = ProfileUi(
                        displayName = profile.displayName,
                        username = profile.username,
                        bio = profile.bio,
                        location = profile.location,
                        website = profile.website,
                        joinedLabel = profile.joinedLabel,
                        postsCount = postsResult.getOrDefault(emptyList()).size,
                        followersCount = followersResult.getOrDefault(0),
                        followingCount = followingResult.getOrDefault(0),
                        avatarUrl = profile.avatarUrl,
                        coverUrl = profile.coverUrl,
                        posts = postsResult.getOrDefault(emptyList()),
                        isSelfProfile = targetUserId == currentUser.id
                    ),
                    error = null
                )
            }
        }
    }

    fun onAvatarSelected(localUri: String) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val user = withContext(dispatchers.io) { getCurrentUserUseCase.execute() } ?: return@launch
            var publicUrl: String? = null
            var uploadError: String? = null

            withContext(dispatchers.io) {
                uploadPostImageUseCase.execute(localUri).collect { progress ->
                    when (progress) {
                        is UploadProgress.Success -> publicUrl = progress.publicUrl
                        is UploadProgress.Error -> uploadError = progress.message.ifBlank { "Upload avatar impossible" }
                        else -> Unit
                    }
                }
            }

            if (uploadError != null || publicUrl.isNullOrBlank()) {
                _uiState.update {
                    it.copy(isLoading = false, error = UiText.DynamicString(uploadError ?: "Upload avatar impossible"))
                }
                return@launch
            }

            withContext(dispatchers.io) {
                updateAvatarUseCase.execute(userId = user.id, avatarUrl = publicUrl!!)
            }.onSuccess { load() }
             .onFailure {
                 _uiState.update {
                     it.copy(isLoading = false, error = UiText.Resource(Res.string.profile_load_error))
                 }
             }
        }
    }

    fun logout() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            withContext(dispatchers.io) { logoutUseCase.execute() }
                .onSuccess {
                    NavigationStore.clear()
                    sessionManager.setUser(null)
                    ProfileTargetStore.openSelf()
                    _uiState.update { ProfileUiState(isLoading = false) }
                    _events.tryEmit(ProfileUiEvent.Navigate(Screen.Login))
                }
                .onFailure {
                    _uiState.update {
                        it.copy(isLoading = false, error = UiText.Resource(Res.string.profile_load_error))
                    }
                }
        }
    }

    fun clear() = job.cancel()
}
