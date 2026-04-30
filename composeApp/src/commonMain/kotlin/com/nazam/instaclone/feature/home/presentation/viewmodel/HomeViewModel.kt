package com.nazam.instaclone.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.session.SessionManager
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LogoutUseCase
import com.nazam.instaclone.feature.home.domain.model.FeedMode
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.domain.usecase.AddCommentUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetCommentsUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetFeedUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteLeftUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteRightUseCase
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import com.nazam.instaclone.feature.home.presentation.vote.VoteIntentStore
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.home_auth_required_comment
import instaclone.composeapp.generated.resources.home_auth_required_create
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val MAX_COMMENT_LENGTH = 500

class HomeViewModel(
    internal val dispatchers: AppDispatchers,
    internal val getFeedUseCase: GetFeedUseCase,
    internal val voteLeftUseCase: VoteLeftUseCase,
    internal val voteRightUseCase: VoteRightUseCase,
    internal val getCommentsUseCase: GetCommentsUseCase,
    internal val addCommentUseCase: AddCommentUseCase,
    internal val getCurrentUserUseCase: GetCurrentUserUseCase,
    internal val logoutUseCase: LogoutUseCase,
    internal val sessionManager: SessionManager
) : ViewModel() {

    internal val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<HomeUiEvent> = _events.asSharedFlow()

    internal var pendingVoteAfterLogin: VoteIntentStore.VoteIntent? = null

    init {
        refreshSession()
        loadFeed()
    }

    fun refreshSession() {
        viewModelScope.launch {
            val user = withContext(dispatchers.io) { getCurrentUserUseCase.execute() }
            sessionManager.setUser(user)

            _uiState.update {
                it.copy(
                    isLoggedIn = user != null,
                    currentUserId = user?.id,
                    currentUserEmail = user?.email,
                    currentUserDisplayName = user?.displayName
                )
            }

            if (user != null && pendingVoteAfterLogin == null) {
                pendingVoteAfterLogin = VoteIntentStore.consume()
            }
            runPendingVoteIfPossible()
        }
    }

    fun loadFeed() = loadFeedInternal(reset = true)
    fun loadMore() = loadFeedInternal(reset = false)

    fun onFeedModeSelected(mode: FeedMode) {
        if (_uiState.value.selectedFeedMode == mode) return
        _uiState.update { it.copy(selectedFeedMode = mode, posts = emptyList(), endReached = false) }
        loadFeed()
    }

    fun voteLeft(postId: String) = voteInternal(postId, isLeft = true)
    fun voteRight(postId: String) = voteInternal(postId, isLeft = false)

    fun onCreatePostClicked() {
        if (uiState.value.isLoggedIn) {
            navigateTo(Screen.CreatePost)
        } else {
            NavigationStore.setAfterLogin(Screen.CreatePost)
            showAuthRequiredDialogInternal(UiText.Resource(Res.string.home_auth_required_create))
        }
    }

    fun openComments(postId: String) = openCommentsInternal(postId)
    fun closeComments() = closeCommentsInternal()

    fun onNewCommentChange(value: String) {
        _uiState.update { it.copy(newCommentText = value.take(MAX_COMMENT_LENGTH)) }
    }

    fun onSendCommentClicked() = sendCommentInternal()

    fun onCommentInputRequested() {
        if (!uiState.value.isLoggedIn) {
            showAuthRequiredDialogInternal(UiText.Resource(Res.string.home_auth_required_comment))
        }
    }

    fun onShareClicked(post: VsPost) {
        _events.tryEmit(HomeUiEvent.Share(post))
    }

    fun logout() = logoutInternal()

    fun onDialogConfirmClicked() {
        val goLogin = uiState.value.dialogShouldOpenLogin
        consumeDialog()
        if (goLogin) navigateTo(Screen.Login)
    }

    fun onDialogSecondaryClicked() {
        val goSignup = uiState.value.dialogShouldOpenSignup
        consumeDialog()
        if (goSignup) navigateTo(Screen.Signup)
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

    internal fun emitMessage(message: UiText) { _events.tryEmit(HomeUiEvent.ShowMessage(message)) }
    internal fun navigateTo(screen: Screen) { _events.tryEmit(HomeUiEvent.Navigate(screen)) }
}
