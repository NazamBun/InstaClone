package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.session.SessionManager
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LogoutUseCase
import com.nazam.instaclone.feature.home.domain.model.VoteCategory
import com.nazam.instaclone.feature.home.domain.usecase.AddCommentUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetCommentsUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetFeedUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteLeftUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteRightUseCase
import com.nazam.instaclone.feature.home.presentation.categories.HomeFilterStore
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import com.nazam.instaclone.feature.home.presentation.vote.VoteIntentStore
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.home_auth_required_comment
import instaclone.composeapp.generated.resources.home_auth_required_create
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val dispatchers: AppDispatchers,
    private val getFeedUseCase: GetFeedUseCase,
    private val voteLeftUseCase: VoteLeftUseCase,
    private val voteRightUseCase: VoteRightUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val sessionManager: SessionManager
) {
    internal val job = SupervisorJob()
    internal val scope = CoroutineScope(job + dispatchers.main)

    internal val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _events = MutableSharedFlow<HomeUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<HomeUiEvent> = _events

    /**
     * Vote en attente après login.
     * On ne le lance qu'une fois et seulement quand le feed est chargé.
     */
    internal var pendingVoteAfterLogin: VoteIntentStore.VoteIntent? = null

    init {
        refreshFilter()
        refreshSession()
        loadFeed()
    }

    fun refreshSession() {
        scope.launch {
            val user = withContext(dispatchers.default) { getCurrentUserUseCase.execute() }

            // ✅ session globale
            sessionManager.setUser(user)

            _uiState.update {
                it.copy(
                    isLoggedIn = user != null,
                    currentUserId = user?.id,
                    currentUserEmail = user?.email,
                    currentUserDisplayName = user?.displayName
                )
            }

            // ✅ si on est connecté maintenant, on récupère un vote en attente
            if (user != null && pendingVoteAfterLogin == null) {
                pendingVoteAfterLogin = VoteIntentStore.consume()
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
            showAuthRequiredDialogInternal(UiText.Resource(Res.string.home_auth_required_create))
        }
    }

    fun openComments(postId: String) = openCommentsInternal(dispatchers, postId, getCommentsUseCase)
    fun closeComments() = closeCommentsInternal()

    fun onNewCommentChange(value: String) {
        _uiState.update { it.copy(newCommentText = value.take(500)) }
    }

    fun onSendCommentClicked() = sendCommentInternal(dispatchers, addCommentUseCase)

    fun onCommentInputRequested() {
        if (!uiState.value.isLoggedIn) {
            showAuthRequiredDialogInternal(UiText.Resource(Res.string.home_auth_required_comment))
        }
    }

    fun logout() = logoutInternal(dispatchers, logoutUseCase, sessionManager)

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

    internal fun emitMessage(message: UiText) {
        _events.tryEmit(HomeUiEvent.ShowMessage(message))
    }

    internal fun navigateTo(screen: Screen) {
        _events.tryEmit(HomeUiEvent.Navigate(screen))
    }

    fun clear() {
        job.cancel()
    }

    fun refreshFilter() {
        val selected = HomeFilterStore.getCategory()
        _uiState.update { it.copy(selectedCategoryId = selected) }
    }

    fun onChooseCategoryFilterClicked() {
        navigateTo(Screen.Explore)
    }

    fun onHomeClicked() {
        HomeFilterStore.clear()
        refreshFilter()
    }

    fun onExploreCategoryClicked(category: VoteCategory) {
        HomeFilterStore.setCategory(category.id)
        refreshFilter()
    }

    fun onExploreClearCategory() {
        HomeFilterStore.clear()
        refreshFilter()
    }
}