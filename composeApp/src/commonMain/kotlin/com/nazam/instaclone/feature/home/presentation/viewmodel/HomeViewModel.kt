package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LogoutUseCase
import com.nazam.instaclone.feature.home.domain.usecase.AddCommentUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetCommentsUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetFeedUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteLeftUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteRightUseCase
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeViewModel : KoinComponent {

    private val getFeedUseCase: GetFeedUseCase by inject()
    private val voteLeftUseCase: VoteLeftUseCase by inject()
    private val voteRightUseCase: VoteRightUseCase by inject()
    private val getCommentsUseCase: GetCommentsUseCase by inject()
    private val addCommentUseCase: AddCommentUseCase by inject()
    private val getCurrentUserUseCase: GetCurrentUserUseCase by inject()
    private val logoutUseCase: LogoutUseCase by inject()

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.Main)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        refreshSession()
        loadFeed()
    }

    fun refreshSession() {
        scope.launch {
            val user = withContext(Dispatchers.Default) { getCurrentUserUseCase.execute() }
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

    fun loadFeed() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = withContext(Dispatchers.Default) { getFeedUseCase.execute() }

            result
                .onSuccess { posts ->
                    _uiState.update { it.copy(isLoading = false, posts = posts) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    showInfoDialog(error.message ?: "Erreur de chargement")
                }
        }
    }

    fun voteLeft(postId: String) {
        if (!uiState.value.isLoggedIn) {
            showAuthRequiredDialog("Tu dois être connecté ou créer un compte pour voter.")
            return
        }
        if (uiState.value.votingPostId == postId) return

        scope.launch {
            _uiState.update { it.copy(votingPostId = postId) }

            val result = withContext(Dispatchers.Default) { voteLeftUseCase.execute(postId) }

            result
                .onSuccess { updated ->
                    _uiState.update { state ->
                        state.copy(
                            votingPostId = null,
                            posts = state.posts.map { post ->
                                if (post.id == updated.id) updated else post
                            }
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(votingPostId = null) }
                    handleAuthOrGenericError(error)
                }
        }
    }

    fun voteRight(postId: String) {
        if (!uiState.value.isLoggedIn) {
            showAuthRequiredDialog("Tu dois être connecté ou créer un compte pour voter.")
            return
        }
        if (uiState.value.votingPostId == postId) return

        scope.launch {
            _uiState.update { it.copy(votingPostId = postId) }

            val result = withContext(Dispatchers.Default) { voteRightUseCase.execute(postId) }

            result
                .onSuccess { updated ->
                    _uiState.update { state ->
                        state.copy(
                            votingPostId = null,
                            posts = state.posts.map { post ->
                                if (post.id == updated.id) updated else post
                            }
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(votingPostId = null) }
                    handleAuthOrGenericError(error)
                }
        }
    }

    fun onCreatePostClicked() {
        if (uiState.value.isLoggedIn) return
        showAuthRequiredDialog("Tu dois être connecté pour créer un post.")
    }

    fun openComments(postId: String) {
        _uiState.update {
            it.copy(
                isCommentsSheetOpen = true,
                commentsPostId = postId,
                isCommentsLoading = true,
                comments = emptyList(),
                newCommentText = ""
            )
        }
        loadComments(postId)
    }

    fun closeComments() {
        _uiState.update {
            it.copy(
                isCommentsSheetOpen = false,
                commentsPostId = null,
                isCommentsLoading = false,
                comments = emptyList(),
                newCommentText = ""
            )
        }
    }

    private fun loadComments(postId: String) {
        scope.launch {
            _uiState.update { it.copy(isCommentsLoading = true) }

            val result = withContext(Dispatchers.Default) { getCommentsUseCase.execute(postId) }

            result
                .onSuccess { list ->
                    _uiState.update { it.copy(isCommentsLoading = false, comments = list) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isCommentsLoading = false) }
                    showInfoDialog(error.message ?: "Impossible de charger les commentaires")
                }
        }
    }

    fun onNewCommentChange(value: String) {
        _uiState.update { it.copy(newCommentText = value.take(500)) }
    }

    fun onSendCommentClicked() {
        val state = uiState.value
        if (!state.isLoggedIn) {
            showAuthRequiredDialog("Tu dois te connecter ou créer un compte pour commenter.")
            return
        }
        submitComment()
    }

    fun onCommentInputRequested() {
        if (!uiState.value.isLoggedIn) {
            showAuthRequiredDialog("Tu dois te connecter ou créer un compte pour commenter.")
        }
    }

    private fun submitComment() {
        val state = uiState.value
        val postId = state.commentsPostId ?: return
        val content = state.newCommentText.trim()
        if (content.isBlank()) return

        scope.launch {
            _uiState.update { it.copy(isCommentsLoading = true) }

            val result = withContext(Dispatchers.Default) {
                addCommentUseCase.execute(postId = postId, content = content)
            }

            result
                .onSuccess { created ->
                    _uiState.update {
                        it.copy(
                            isCommentsLoading = false,
                            comments = it.comments + created,
                            newCommentText = ""
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isCommentsLoading = false) }
                    handleAuthOrGenericError(error)
                }
        }
    }

    fun logout() {
        scope.launch {
            val result = withContext(Dispatchers.Default) { logoutUseCase.execute() }

            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoggedIn = false,
                            currentUserId = null,
                            currentUserEmail = null,
                            currentUserDisplayName = null
                        )
                    }
                    showInfoDialog("Déconnecté")
                    loadFeed()
                }
                .onFailure { error ->
                    showInfoDialog(error.message ?: "Erreur de déconnexion")
                }
        }
    }

    private fun handleAuthOrGenericError(error: Throwable) {
        if (error is IllegalStateException && error.message == "AUTH_REQUIRED") {
            showAuthRequiredDialog("Tu dois être connecté ou créer un compte.")
        } else {
            showInfoDialog(error.message ?: "Une erreur est arrivée")
        }
    }

    private fun showAuthRequiredDialog(message: String) {
        _uiState.update {
            it.copy(
                dialogMessage = message,
                dialogConfirmLabel = "Se connecter",
                dialogSecondaryLabel = "Créer un compte",
                dialogShouldOpenLogin = true,
                dialogShouldOpenSignup = true
            )
        }
    }

    private fun showInfoDialog(message: String) {
        _uiState.update {
            it.copy(
                dialogMessage = message,
                dialogConfirmLabel = null,
                dialogSecondaryLabel = null,
                dialogShouldOpenLogin = false,
                dialogShouldOpenSignup = false
            )
        }
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

    fun clear() {
        job.cancel()
    }
}