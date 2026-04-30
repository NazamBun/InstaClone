package com.nazam.instaclone.feature.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.session.SessionManager
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.auth.domain.model.AuthUser
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LogoutUseCase
import com.nazam.instaclone.feature.home.domain.model.UploadProgress
import com.nazam.instaclone.feature.home.domain.usecase.UploadPostImageUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.FollowUserUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.GetFollowersCountUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.GetFollowingCountUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.GetMyPostsUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.GetMyProfileUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.IsFollowingUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.UnfollowUserUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.UpdateAvatarUseCase
import com.nazam.instaclone.feature.profile.presentation.model.ProfileUiState
import com.nazam.instaclone.feature.profile.presentation.navigation.ProfileTargetStore
import com.nazam.instaclone.feature.profile.presentation.ui.ProfileUi
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.profile_avatar_upload_error
import instaclone.composeapp.generated.resources.profile_follow_error
import instaclone.composeapp.generated.resources.profile_load_error
import instaclone.composeapp.generated.resources.profile_logout_error
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val isFollowingUseCase: IsFollowingUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val unfollowUserUseCase: UnfollowUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val sessionManager: SessionManager,
    private val uploadPostImageUseCase: UploadPostImageUseCase,
    private val updateAvatarUseCase: UpdateAvatarUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProfileUiEvent> = _events.asSharedFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val current = withContext(dispatchers.io) { getCurrentUserUseCase.execute() }
            val targetUserId = ProfileTargetStore.getUserId() ?: current?.id
            val targetEmail = ProfileTargetStore.getEmailFallback() ?: current?.email

            if (targetUserId.isNullOrBlank() || targetEmail.isNullOrBlank()) {
                return@launch failLoad()
            }

            val profile = withContext(dispatchers.io) {
                getMyProfileUseCase.execute(targetUserId, targetEmail).getOrNull()
            } ?: return@launch failLoad()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    ui = buildProfileUi(targetUserId, profile, current),
                    error = null
                )
            }
        }
    }

    fun onFollowClicked() {
        val ui = _uiState.value.ui ?: return
        if (ui.isSelfProfile) return

        viewModelScope.launch {
            val current = withContext(dispatchers.io) { getCurrentUserUseCase.execute() }
            if (current == null) {
                NavigationStore.setAfterLogin(Screen.UserProfile)
                _events.tryEmit(ProfileUiEvent.Navigate(Screen.Login))
                return@launch
            }

            _uiState.update { it.copy(ui = it.ui?.copy(isFollowLoading = true)) }

            val result = withContext(dispatchers.io) {
                if (ui.isFollowing) unfollowUserUseCase.execute(current.id, ui.userId)
                else followUserUseCase.execute(current.id, ui.userId)
            }

            result
                .onSuccess { applyFollowToggle() }
                .onFailure { setError(UiText.Resource(Res.string.profile_follow_error)) }
        }
    }

    fun onAvatarSelected(localUri: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val user = withContext(dispatchers.io) { getCurrentUserUseCase.execute() }
                ?: return@launch failAvatar()

            var publicUrl: String? = null
            withContext(dispatchers.io) {
                uploadPostImageUseCase.execute(localUri).collect { progress ->
                    if (progress is UploadProgress.Success) publicUrl = progress.publicUrl
                }
            }
            val url = publicUrl ?: return@launch failAvatar()

            withContext(dispatchers.io) { updateAvatarUseCase.execute(user.id, url) }
                .onSuccess { load() }
                .onFailure { failAvatar() }
        }
    }

    fun logout() {
        viewModelScope.launch {
            withContext(dispatchers.io) { logoutUseCase.execute() }
                .onSuccess {
                    NavigationStore.clear()
                    sessionManager.setUser(null)
                    ProfileTargetStore.openSelf()
                    _uiState.update { ProfileUiState(isLoading = false) }
                    _events.tryEmit(ProfileUiEvent.Navigate(Screen.Login))
                }
                .onFailure { setError(UiText.Resource(Res.string.profile_logout_error)) }
        }
    }

    private suspend fun buildProfileUi(
        targetUserId: String,
        profile: com.nazam.instaclone.feature.profile.domain.model.Profile,
        current: AuthUser?
    ): ProfileUi {
        val posts = withContext(dispatchers.io) { getMyPostsUseCase.execute(targetUserId).getOrDefault(emptyList()) }
        val followers = withContext(dispatchers.io) { getFollowersCountUseCase.execute(targetUserId).getOrDefault(0) }
        val following = withContext(dispatchers.io) { getFollowingCountUseCase.execute(targetUserId).getOrDefault(0) }
        val isSelf = current?.id == targetUserId
        val isFollowing = if (isSelf || current == null) false else withContext(dispatchers.io) {
            isFollowingUseCase.execute(current.id, targetUserId).getOrDefault(false)
        }
        return ProfileUi(
            userId = targetUserId,
            displayName = profile.displayName, username = profile.username,
            bio = profile.bio, location = profile.location, website = profile.website,
            joinedLabel = profile.joinedLabel,
            postsCount = posts.size, followersCount = followers, followingCount = following,
            avatarUrl = profile.avatarUrl, coverUrl = profile.coverUrl, posts = posts,
            isSelfProfile = isSelf, isFollowing = isFollowing
        )
    }

    private fun applyFollowToggle() = _uiState.update { state ->
        val current = state.ui ?: return@update state
        val nowFollowing = !current.isFollowing
        val followers = if (nowFollowing) current.followersCount + 1
        else (current.followersCount - 1).coerceAtLeast(0)
        state.copy(ui = current.copy(isFollowing = nowFollowing, followersCount = followers, isFollowLoading = false))
    }

    private fun failLoad() = _uiState.update {
        it.copy(isLoading = false, ui = null, error = UiText.Resource(Res.string.profile_load_error))
    }

    private fun failAvatar() = _uiState.update {
        it.copy(isLoading = false, error = UiText.Resource(Res.string.profile_avatar_upload_error))
    }

    private fun setError(error: UiText) = _uiState.update { state ->
        state.copy(ui = state.ui?.copy(isFollowLoading = false), error = error)
    }
}
