package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.session.SessionManager
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.auth.domain.usecase.LogoutUseCase
import com.nazam.instaclone.feature.home.domain.model.FeedMode
import com.nazam.instaclone.feature.home.domain.usecase.AddCommentUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetCommentsUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetFeedUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteLeftUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteRightUseCase
import com.nazam.instaclone.feature.home.presentation.vote.VoteIntentStore
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.dialog_login
import instaclone.composeapp.generated.resources.dialog_signup
import instaclone.composeapp.generated.resources.error_unknown
import instaclone.composeapp.generated.resources.home_auth_required_generic
import instaclone.composeapp.generated.resources.home_auth_required_vote
import instaclone.composeapp.generated.resources.home_comments_load_error
import instaclone.composeapp.generated.resources.home_feed_error
import instaclone.composeapp.generated.resources.home_feed_loaded
import instaclone.composeapp.generated.resources.home_logged_out
import instaclone.composeapp.generated.resources.home_logout_error
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal fun HomeViewModel.loadFeedInternal(
    dispatchers: AppDispatchers,
    getFeedUseCase: GetFeedUseCase,
    reset: Boolean
) {
    val state = uiState.value
    if (reset && state.isLoading) return
    if (!reset && (state.isLoadingMore || state.endReached)) return

    val pageSize = 10
    val offset = if (reset) 0 else state.posts.size
    val mode = state.selectedFeedMode

    scope.launch {
        _uiState.update {
            if (reset) it.copy(isLoading = true, isLoadingMore = false, endReached = false)
            else it.copy(isLoadingMore = true)
        }

        val result = withContext(dispatchers.default) {
            getFeedUseCase.execute(mode = mode, offset = offset, limit = pageSize)
        }

        result.onSuccess { page ->
            _uiState.update { current ->
                val merged = if (reset) page else (current.posts + page).distinctBy { it.id }
                current.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    posts = merged,
                    endReached = page.size < pageSize
                )
            }

            if (reset) {
                val msg = if (mode == FeedMode.FOLLOWING && page.isEmpty()) {
                    UiText.DynamicString("Aucun post suivi pour l'instant")
                } else {
                    UiText.ResourceArgs(Res.string.home_feed_loaded, listOf(uiState.value.posts.size))
                }
                emitMessage(msg)
                runPendingVoteIfPossible()
            }
        }.onFailure { error ->
            _uiState.update { it.copy(isLoading = false, isLoadingMore = false) }
            emitMessage(
                error.message?.takeIf { it.isNotBlank() }?.let(UiText::DynamicString)
                    ?: UiText.Resource(Res.string.home_feed_error)
            )
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
        VoteIntentStore.save(postId, if (isLeft) VoteIntentStore.Side.LEFT else VoteIntentStore.Side.RIGHT)
        NavigationStore.setAfterLogin(Screen.Home)
        showAuthRequiredDialogInternal(UiText.Resource(Res.string.home_auth_required_vote))
        return
    }
    if (state.votingPostId == postId) return

    scope.launch {
        _uiState.update { it.copy(votingPostId = postId) }
        val result = withContext(dispatchers.default) {
            if (isLeft) voteLeftUseCase.execute(postId) else voteRightUseCase.execute(postId)
        }
        result.onSuccess { updated ->
            _uiState.update { s ->
                s.copy(votingPostId = null, posts = s.posts.map { if (it.id == updated.id) updated else it })
            }
        }.onFailure {
            _uiState.update { it.copy(votingPostId = null) }
            handleAuthOrGenericErrorInternal(it)
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
            isSendingComment = false,
            comments = emptyList(),
            newCommentText = ""
        )
    }

    scope.launch {
        withContext(dispatchers.default) { getCommentsUseCase.execute(postId) }
            .onSuccess { comments ->
                _uiState.update { s ->
                    s.copy(
                        isCommentsLoading = false,
                        comments = comments
                    )
                }
            }
            .onFailure {
                _uiState.update { s -> s.copy(isCommentsLoading = false) }
                emitMessage(
                    it.message?.takeIf(String::isNotBlank)?.let(UiText::DynamicString)
                        ?: UiText.Resource(Res.string.home_comments_load_error)
                )
            }
    }
}

internal fun HomeViewModel.closeCommentsInternal() {
    _uiState.update {
        it.copy(
            isCommentsSheetOpen = false,
            commentsPostId = null,
            isCommentsLoading = false,
            isSendingComment = false,
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
        showAuthRequiredDialogInternal(UiText.Resource(Res.string.home_auth_required_generic))
        return
    }

    val postId = state.commentsPostId ?: return
    val content = state.newCommentText.trim()
    if (content.isBlank() || state.isSendingComment) return

    scope.launch {
        _uiState.update { it.copy(isSendingComment = true) }

        withContext(dispatchers.default) { addCommentUseCase.execute(postId, content) }
            .onSuccess { created ->
                _uiState.update { current ->
                    current.copy(
                        isSendingComment = false,
                        comments = current.comments + created,
                        newCommentText = "",
                        posts = current.posts.map { post ->
                            if (post.id == postId) {
                                post.copy(commentsCount = post.commentsCount + 1)
                            } else {
                                post
                            }
                        }
                    )
                }
            }
            .onFailure {
                _uiState.update { it.copy(isSendingComment = false) }
                handleAuthOrGenericErrorInternal(it)
            }
    }
}

internal fun HomeViewModel.logoutInternal(
    dispatchers: AppDispatchers,
    logoutUseCase: LogoutUseCase,
    sessionManager: SessionManager
) {
    scope.launch {
        withContext(dispatchers.default) { logoutUseCase.execute() }
            .onSuccess {
                NavigationStore.clear()
                VoteIntentStore.clear()
                pendingVoteAfterLogin = null
                sessionManager.setUser(null)
                _uiState.update {
                    it.copy(
                        isLoggedIn = false,
                        currentUserId = null,
                        currentUserEmail = null,
                        currentUserDisplayName = null,
                        isCommentsSheetOpen = false,
                        commentsPostId = null,
                        isCommentsLoading = false,
                        comments = emptyList(),
                        newCommentText = ""
                    )
                }
                emitMessage(UiText.Resource(Res.string.home_logged_out))
                navigateTo(Screen.Login)
            }
            .onFailure {
                emitMessage(it.message?.takeIf(String::isNotBlank)?.let(UiText::DynamicString)
                    ?: UiText.Resource(Res.string.home_logout_error))
            }
    }
}

internal fun HomeViewModel.handleAuthOrGenericErrorInternal(error: Throwable) {
    if (error.isAuthRequired()) showAuthRequiredDialogInternal(UiText.Resource(Res.string.home_auth_required_generic))
    else emitMessage(error.message?.takeIf(String::isNotBlank)?.let(UiText::DynamicString)
        ?: UiText.Resource(Res.string.error_unknown))
}

internal fun HomeViewModel.showAuthRequiredDialogInternal(message: UiText) {
    _uiState.update {
        it.copy(
            dialogMessage = message,
            dialogConfirmLabel = UiText.Resource(Res.string.dialog_login),
            dialogSecondaryLabel = UiText.Resource(Res.string.dialog_signup),
            dialogShouldOpenLogin = true,
            dialogShouldOpenSignup = true
        )
    }
}

internal fun HomeViewModel.runPendingVoteIfPossible() {
    val state = uiState.value
    if (!state.isLoggedIn || state.isLoading) return

    if (pendingVoteAfterLogin == null) pendingVoteAfterLogin = VoteIntentStore.consume()
    val intent = pendingVoteAfterLogin ?: return
    if (state.posts.none { it.id == intent.postId }) return

    pendingVoteAfterLogin = null
    when (intent.side) {
        VoteIntentStore.Side.LEFT -> voteLeft(intent.postId)
        VoteIntentStore.Side.RIGHT -> voteRight(intent.postId)
    }
}
