package com.nazam.instaclone.feature.profile.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.session.SessionManager
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LogoutUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.GetMyPostsUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.GetMyProfileUseCase
import com.nazam.instaclone.feature.profile.presentation.model.ProfileUiState
import com.nazam.instaclone.feature.profile.presentation.ui.ProfileUi
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.profile_load_error
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    private val dispatchers: AppDispatchers,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val getMyPostsUseCase: GetMyPostsUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val sessionManager: SessionManager
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

            val user = withContext(dispatchers.io) { getCurrentUserUseCase.execute() }

            if (user == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        ui = null,
                        error = UiText.Resource(Res.string.profile_load_error)
                    )
                }
                return@launch
            }

            val profileResult = withContext(dispatchers.io) {
                getMyProfileUseCase.execute(userId = user.id, emailFallback = user.email)
            }

            val postsResult = withContext(dispatchers.io) {
                getMyPostsUseCase.execute(email = user.email)
            }

            val profile = profileResult.getOrNull()
            val posts = postsResult.getOrNull().orEmpty()

            if (profile == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        ui = null,
                        error = UiText.Resource(Res.string.profile_load_error)
                    )
                }
                return@launch
            }

            val isSelf = profile.userId == user.id

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
                        postsCount = posts.size,
                        followersCount = 0,
                        followingCount = 0,
                        avatarUrl = profile.avatarUrl,
                        coverUrl = profile.coverUrl,
                        posts = posts,
                        isSelfProfile = isSelf
                    ),
                    error = null
                )
            }
        }
    }

    fun logout() {
        scope.launch {

            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = withContext(dispatchers.io) { logoutUseCase.execute() }

            result
                .onSuccess {
                    NavigationStore.clear()
                    sessionManager.setUser(null)

                    _uiState.update { ProfileUiState(isLoading = false) }

                    _events.tryEmit(ProfileUiEvent.Navigate(Screen.Login))
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = UiText.Resource(Res.string.profile_load_error)
                        )
                    }
                }
        }
    }

    fun clear() {
        job.cancel()
    }
}
