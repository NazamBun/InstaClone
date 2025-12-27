package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LogoutUseCase
import com.nazam.instaclone.feature.home.domain.usecase.AddCommentUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetCommentsUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetFeedUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteLeftUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteRightUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private val HomeViewModel.koin get() = object : KoinComponent {}

private val HomeViewModel.getFeedUseCase: GetFeedUseCase get() = koin.inject<GetFeedUseCase>().value
private val HomeViewModel.voteLeftUseCase: VoteLeftUseCase get() = koin.inject<VoteLeftUseCase>().value
private val HomeViewModel.voteRightUseCase: VoteRightUseCase get() = koin.inject<VoteRightUseCase>().value
private val HomeViewModel.getCommentsUseCase: GetCommentsUseCase get() = koin.inject<GetCommentsUseCase>().value
private val HomeViewModel.addCommentUseCase: AddCommentUseCase get() = koin.inject<AddCommentUseCase>().value
private val HomeViewModel.getCurrentUserUseCase: GetCurrentUserUseCase get() = koin.inject<GetCurrentUserUseCase>().value
private val HomeViewModel.logoutUseCase: LogoutUseCase get() = koin.inject<LogoutUseCase>().value

internal fun HomeViewModel.refreshSession() {
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

internal fun HomeViewModel.loadFeed() {
    scope.launch {
        _uiState.update { it.copy(isLoading = true) }

        val result = withContext(Dispatchers.Default) { getFeedUseCase.execute() }

        result
            .onSuccess { posts -> _uiState.update { it.copy(isLoading = false, posts = posts) } }
            .onFailure { error ->
                _uiState.update { it.copy(isLoading = false) }
                showInfoDialog(error.message ?: "Erreur de chargement")
            }
    }
}

internal fun HomeViewModel.vote(postId: String, isLeft: Boolean) {
    if (!uiState.value.isLoggedIn) {
        showAuthRequiredDialog("Tu dois être connecté ou créer un compte pour voter.")
        return
    }
    if (uiState.value.votingPostId == postId) return

    scope.launch {
        _uiState.update { it.copy(votingPostId = postId) }

        val result = withContext(Dispatchers.Default) {
            if (isLeft) voteLeftUseCase.execute(postId) else voteRightUseCase.execute(postId)
        }

        result
            .onSuccess { updated ->
                _uiState.update { state ->
                    state.copy(
                        votingPostId = null,
                        posts = state.posts.map { post -> if (post.id == updated.id) updated else post }
                    )
                }
            }
            .onFailure { error ->
                _uiState.update { it.copy(votingPostId = null) }
                handleAuthOrGenericError(error)
            }
    }
}

internal fun HomeViewModel.loadComments(postId: String) {
    scope.launch {
        _uiState.update { it.copy(isCommentsLoading = true) }

        val result = withContext(Dispatchers.Default) { getCommentsUseCase.execute(postId) }

        result
            .onSuccess { list -> _uiState.update { it.copy(isCommentsLoading = false, comments = list) } }
            .onFailure { error ->
                _uiState.update { it.copy(isCommentsLoading = false) }
                showInfoDialog(error.message ?: "Impossible de charger les commentaires")
            }
    }
}

internal fun HomeViewModel.submitComment() {
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

internal fun HomeViewModel.doLogout() {
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