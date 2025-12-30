package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LogoutUseCase
import com.nazam.instaclone.feature.home.domain.usecase.AddCommentUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetCommentsUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetFeedUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteLeftUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteRightUseCase
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel KMP pur:
 * - UiState: état durable
 * - events: navigation + message (one-shot)
 */
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
            val user = withContext(dispatchers.default) { getCurrentUserUseCase.execute() }
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

    fun voteLeft(postId: String) = voteInternal(dispatchers, postId, true, voteLeftUseCase, voteRightUseCase)

    fun voteRight(postId: String) = voteInternal(dispatchers, postId, false, voteLeftUseCase, voteRightUseCase)

    fun onCreatePostClicked() {
        if (uiState.value.isLoggedIn) {
            _events.tryEmit(HomeUiEvent.NavigateToCreatePost)
        } else {
            showAuthRequiredDialogInternal("Tu dois être connecté pour créer un post.")
        }
    }

    fun onLoginClicked() {
        _events.tryEmit(HomeUiEvent.NavigateToLogin)
    }

    fun openComments(postId: String) = openCommentsInternal(dispatchers, postId, getCommentsUseCase)

    fun closeComments() = closeCommentsInternal()

    fun onNewCommentChange(value: String) {
        _uiState.update { it.copy(newCommentText = value.take(500)) }
    }

    fun onSendCommentClicked() = sendCommentInternal(dispatchers, addCommentUseCase)

    fun onCommentInputRequested() {
        if (!uiState.value.isLoggedIn) {
            showAuthRequiredDialogInternal("Tu dois te connecter ou créer un compte pour commenter.")
        }
    }

    fun logout() = logoutInternal(dispatchers, logoutUseCase)

    fun onDialogConfirmClicked() {
        val goLogin = uiState.value.dialogShouldOpenLogin
        consumeDialog()
        if (goLogin) _events.tryEmit(HomeUiEvent.NavigateToLogin)
    }

    fun onDialogSecondaryClicked() {
        val goSignup = uiState.value.dialogShouldOpenSignup
        consumeDialog()
        if (goSignup) _events.tryEmit(HomeUiEvent.NavigateToSignup)
    }

    fun consumeDialog() {
        _uiState.update {
            it.copy(
                dialogMessage = null,
                dialogConfirmLabel = null,
                dialogSecondaryLabel = null,
                dialogShouldOpenLogin = false,
                dialogShouldOpenSignup = false
            )
        }
    }

    internal fun emitMessage(message: String) {
        _events.tryEmit(HomeUiEvent.ShowMessage(message))
    }

    fun clear() {
        job.cancel()
    }
}