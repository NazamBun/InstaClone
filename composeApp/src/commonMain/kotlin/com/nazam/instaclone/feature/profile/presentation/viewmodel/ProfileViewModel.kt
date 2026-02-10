package com.nazam.instaclone.feature.profile.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.profile.domain.usecase.GetMyProfileUseCase
import com.nazam.instaclone.feature.profile.presentation.model.ProfileUiState
import com.nazam.instaclone.feature.profile.presentation.ui.ProfileUi
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.profile_load_error
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    private val dispatchers: AppDispatchers,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase
) {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + dispatchers.main)

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        load()
    }

    fun load() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val user = withContext(dispatchers.default) { getCurrentUserUseCase.execute() }
            if (user == null) {
                _uiState.update { it.copy(isLoading = false, ui = null, error = UiText.Resource(Res.string.profile_load_error)) }
                return@launch
            }

            val result = withContext(dispatchers.default) {
                getMyProfileUseCase.execute(
                    userId = user.id,
                    emailFallback = user.email
                )
            }

            result
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            ui = ProfileUi(
                                displayName = profile.displayName,
                                username = profile.username,
                                bio = profile.bio,
                                location = profile.location,
                                website = profile.website,
                                joinedLabel = profile.joinedLabel,
                                postsCount = 0,
                                followersCount = 0,
                                followingCount = 0,
                                avatarUrl = profile.avatarUrl,
                                coverUrl = profile.coverUrl
                            )
                        )
                    }
                }
                .onFailure {
                    _uiState.update { s ->
                        s.copy(
                            isLoading = false,
                            ui = null,
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
