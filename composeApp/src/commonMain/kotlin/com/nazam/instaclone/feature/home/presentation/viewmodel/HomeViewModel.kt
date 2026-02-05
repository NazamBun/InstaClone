package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LogoutUseCase
import com.nazam.instaclone.feature.home.domain.usecase.*
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class HomeViewModel(
    private val dispatchers: AppDispatchers,
    private val getFeedUseCase: GetFeedUseCase,
    private val voteLeftUseCase: VoteLeftUseCase,
    private val voteRightUseCase: VoteRightUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) {

    internal val job = SupervisorJob()
    internal val scope = CoroutineScope(job + dispatchers.main)

    internal val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _events = MutableSharedFlow<HomeUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<HomeUiEvent> = _events

    init {
        refreshSession()
        loadFeed()
    }

    fun refreshSession() {
        scope.launch {
            val user = withContext(dispatchers.default) {
                getCurrentUserUseCase.execute()
            }
            _uiState.update {
                it.copy(
                    isLoggedIn = user != null,
                    currentUserId = user?.id,
                    currentUserEmail = user?.email,
                    currentUserDisplayName = user?.displayName
                )
            }
        }
    }

    fun loadFeed() = loadFeedInternal(dispatchers, getFeedUseCase)

    fun voteLeft(postId: String) =
        voteInternal(dispatchers, postId, true, voteLeftUseCase, voteRightUseCase)

    fun voteRight(postId: String) =
        voteInternal(dispatchers, postId, false, voteLeftUseCase, voteRightUseCase)

    fun onCreatePostClicked() {
        if (uiState.value.isLoggedIn) {
            navigateTo(Screen.CreatePost)
        } else {
            NavigationStore.setAfterLogin(Screen.CreatePost)
            showAuthRequired()
        }
    }

    fun openComments(postId: String) =
        openCommentsInternal(dispatchers, postId, getCommentsUseCase)

    fun closeComments() = closeCommentsInternal()

    fun onNewCommentChange(value: String) {
        _uiState.update { it.copy(newCommentText = value.take(500)) }
    }

    fun onSendCommentClicked() =
        sendCommentInternal(dispatchers, addCommentUseCase)

    fun onCommentInputRequested() {
        if (!uiState.value.isLoggedIn) {
            showAuthRequired()
        }
    }

    fun logout() = logoutInternal(dispatchers, logoutUseCase)

    internal fun emitMessage(message: UiText) {
        _events.tryEmit(HomeUiEvent.ShowMessage(message))
    }

    internal fun navigateTo(screen: Screen) {
        _events.tryEmit(HomeUiEvent.Navigate(screen))
    }

    private fun showAuthRequired() {
        _uiState.update {
            it.copy(
                dialogMessage = "Tu dois être connecté.",
                dialogConfirmLabel = "Se connecter",
                dialogSecondaryLabel = "Créer un compte",
                dialogShouldOpenLogin = true,
                dialogShouldOpenSignup = true
            )
        }
    }

    fun clear() {
        job.cancel()
    }
}