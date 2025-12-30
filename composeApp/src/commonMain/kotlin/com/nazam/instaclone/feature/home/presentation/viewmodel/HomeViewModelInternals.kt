package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.feature.auth.domain.usecase.LogoutUseCase
import com.nazam.instaclone.feature.home.domain.usecase.AddCommentUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetCommentsUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetFeedUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteLeftUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteRightUseCase
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Logique longue séparée:
 * HomeViewModel reste simple.
 */
internal fun HomeViewModel.loadFeedInternal(
    dispatchers: AppDispatchers,
    getFeedUseCase: GetFeedUseCase
) {
    scope.launch {
        _uiState.update { it.copy(isLoading = true) }

        val result = withContext(dispatchers.default) { getFeedUseCase.execute() }

        result
            .onSuccess { posts ->
                _uiState.update { it.copy(isLoading = false, posts = posts) }
            }
            .onFailure { error ->
                _uiState.update { it.copy(isLoading = false) }
                emitMessage(error.toUiMessage("Erreur de chargement"))
            }
    }
}

internal fun HomeViewModel.voteInternal(
    dispatchers: AppDispatchers,
    postId: String,
    isLeft: Boolean,
    voteLeftUseCase: VoteLeftUseCase,
    voteRightUseCase: VoteRightUseCase
) {
    val state = uiState.value

    if (!state.isLoggedIn) {
        showAuthRequiredDialogInternal("Tu dois être connecté ou créer un compte pour voter.")
        return
    }
    if (state.votingPostId == postId) return

    scope.launch {
        _uiState.update { it.copy(votingPostId = postId) }

        val result = withContext(dispatchers.default) {
            if (isLeft) voteLeftUseCase.execute(postId) else voteRightUseCase.execute(postId)
        }

        result
            .onSuccess { updated ->
                _uiState.update { s ->
                    s.copy(
                        votingPostId = null,
                        posts = s.posts.map { if (it.id == updated.id) updated else it }
                    )
                }
            }
            .onFailure { error ->
                _uiState.update { it.copy(votingPostId = null) }
                handleAuthOrGenericErrorInternal(error)
            }
    }
}

internal fun HomeViewModel.openCommentsInternal(
    dispatchers: AppDispatchers,
    postId: String,
    getCommentsUseCase: GetCommentsUseCase
) {
    _uiState.update {
        it.copy(
            isCommentsSheetOpen = true,
            commentsPostId = postId,
            isCommentsLoading = true,
            comments = emptyList(),
            newCommentText = ""
        )
    }

    scope.launch {
        _uiState.update { it.copy(isCommentsLoading = true) }

        val result = withContext(dispatchers.default) { getCommentsUseCase.execute(postId) }

        result
            .onSuccess { list ->
                _uiState.update { it.copy(isCommentsLoading = false, comments = list) }
            }
            .onFailure { error ->
                _uiState.update { it.copy(isCommentsLoading = false) }
                emitMessage(error.toUiMessage("Impossible de charger les commentaires"))
            }
    }
}

internal fun HomeViewModel.closeCommentsInternal() {
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

internal fun HomeViewModel.sendCommentInternal(
    dispatchers: AppDispatchers,
    addCommentUseCase: AddCommentUseCase
) {
    val state = uiState.value

    if (!state.isLoggedIn) {
        showAuthRequiredDialogInternal("Tu dois te connecter ou créer un compte pour commenter.")
        return
    }

    val postId = state.commentsPostId ?: return
    val content = state.newCommentText.trim()
    if (content.isBlank()) return

    scope.launch {
        _uiState.update { it.copy(isCommentsLoading = true) }

        val result = withContext(dispatchers.default) {
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
                handleAuthOrGenericErrorInternal(error)
            }
    }
}

internal fun HomeViewModel.logoutInternal(
    dispatchers: AppDispatchers,
    logoutUseCase: LogoutUseCase
) {
    scope.launch {
        val result = withContext(dispatchers.default) { logoutUseCase.execute() }

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
                emitMessage("Déconnecté")
                loadFeed()
            }
            .onFailure { error ->
                emitMessage(error.toUiMessage("Erreur de déconnexion"))
            }
    }
}

internal fun HomeViewModel.handleAuthOrGenericErrorInternal(error: Throwable) {
    if (error.isAuthRequired()) {
        showAuthRequiredDialogInternal("Tu dois être connecté ou créer un compte.")
    } else {
        emitMessage(error.toUiMessage("Une erreur est arrivée"))
    }
}

internal fun HomeViewModel.showAuthRequiredDialogInternal(message: String) {
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