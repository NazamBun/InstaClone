// File: composeApp/src/commonMain/kotlin/com/nazam/instaclone/feature/home/presentation/viewmodel/HomeViewModelInternals.kt
package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.navigation.NavigationStore
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.auth.domain.usecase.LogoutUseCase
import com.nazam.instaclone.feature.home.domain.usecase.AddCommentUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetCommentsUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetFeedUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteLeftUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteRightUseCase
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.dialog_login
import instaclone.composeapp.generated.resources.dialog_signup
import instaclone.composeapp.generated.resources.error_unknown
import instaclone.composeapp.generated.resources.home_auth_required_comment
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

/**
 * HomeViewModelInternals :
 * - Fonctions internes pour garder HomeViewModel plus petit et plus lisible.
 * - ✅ Tous les textes UI passent par UiText (KMP + traduisible).
 * - ✅ Les erreurs serveur peuvent rester en DynamicString.
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

                // "Feed chargé : X posts"
                emitMessage(
                    UiText.ResourceArgs(
                        res = Res.string.home_feed_loaded,
                        args = listOf(posts.size)
                    )
                )
            }
            .onFailure { error ->
                _uiState.update { it.copy(isLoading = false) }

                // Message serveur si dispo, sinon message traduisible
                val msg = error.message?.takeIf { it.isNotBlank() }
                emitMessage(
                    msg?.let { UiText.DynamicString(it) }
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
        showAuthRequiredDialogInternal(UiText.Resource(Res.string.home_auth_required_vote))
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
        val result = withContext(dispatchers.default) { getCommentsUseCase.execute(postId) }

        result
            .onSuccess { list ->
                _uiState.update { it.copy(isCommentsLoading = false, comments = list) }
            }
            .onFailure { error ->
                _uiState.update { it.copy(isCommentsLoading = false) }

                val msg = error.message?.takeIf { it.isNotBlank() }
                emitMessage(
                    msg?.let { UiText.DynamicString(it) }
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
        showAuthRequiredDialogInternal(UiText.Resource(Res.string.home_auth_required_comment))
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
                // Nettoyage des intentions de navigation
                NavigationStore.clear()

                // Reset UI local
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
                        newCommentText = "",

                        dialogMessage = null,
                        dialogConfirmLabel = null,
                        dialogSecondaryLabel = null,
                        dialogShouldOpenLogin = false,
                        dialogShouldOpenSignup = false
                    )
                }

                emitMessage(UiText.Resource(Res.string.home_logged_out))
                navigateTo(Screen.Login)
            }
            .onFailure { error ->
                val msg = error.message?.takeIf { it.isNotBlank() }
                emitMessage(
                    msg?.let { UiText.DynamicString(it) }
                        ?: UiText.Resource(Res.string.home_logout_error)
                )
            }
    }
}

internal fun HomeViewModel.handleAuthOrGenericErrorInternal(error: Throwable) {
    if (error.isAuthRequired()) {
        showAuthRequiredDialogInternal(UiText.Resource(Res.string.home_auth_required_generic))
    } else {
        val msg = error.message?.takeIf { it.isNotBlank() }
        emitMessage(
            msg?.let { UiText.DynamicString(it) }
                ?: UiText.Resource(Res.string.error_unknown)
        )
    }
}

/**
 * Ouvre le dialog "auth required" dans l'UiState.
 * ✅ HomeUiState stocke des UiText, et HomeDialogHost fait UiText -> String côté UI.
 */
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