package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel {

    internal val job = SupervisorJob()
    internal val scope = CoroutineScope(job + Dispatchers.Main)

    internal val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        refreshSession()
        loadFeed()
    }

    fun voteLeft(postId: String) = vote(postId, isLeft = true)

    fun voteRight(postId: String) = vote(postId, isLeft = false)

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

    fun onNewCommentChange(value: String) {
        _uiState.update { it.copy(newCommentText = value.take(500)) }
    }

    fun onSendCommentClicked() {
        if (!uiState.value.isLoggedIn) {
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

    fun logout() = doLogout()

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