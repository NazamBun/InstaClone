package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LogoutUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetFeedUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteLeftUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteRightUseCase
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeViewModel : KoinComponent {

    private val getFeedUseCase: GetFeedUseCase by inject()
    private val voteLeftUseCase: VoteLeftUseCase by inject()
    private val voteRightUseCase: VoteRightUseCase by inject()

    private val getCurrentUserUseCase: GetCurrentUserUseCase by inject()
    private val logoutUseCase: LogoutUseCase by inject()

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        refreshSession()
        loadFeed()
    }

    fun refreshSession() {
        scope.launch {
            val user = getCurrentUserUseCase.execute()
            _uiState.update { it.copy(isLoggedIn = user != null) }
        }
    }

    fun loadFeed() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = getFeedUseCase.execute()

            result
                .onSuccess { posts ->
                    _uiState.update { it.copy(isLoading = false, posts = posts) }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                    showInfoDialog("Erreur de chargement")
                }
        }
    }

    fun voteLeft(postId: String) {
        if (_uiState.value.votingPostId == postId) return

        scope.launch {
            _uiState.update { it.copy(votingPostId = postId) }

            val result = voteLeftUseCase.execute(postId)

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
        if (_uiState.value.votingPostId == postId) return

        scope.launch {
            _uiState.update { it.copy(votingPostId = postId) }

            val result = voteRightUseCase.execute(postId)

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

    // ✅ Quand on clique "Créer" (UI)
    fun onCreatePostClicked() {
        if (_uiState.value.isLoggedIn) return
        showLoginDialog("Tu dois être connecté pour créer un post")
    }

    fun logout() {
        scope.launch {
            val result = logoutUseCase.execute()

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoggedIn = false) }
                    showInfoDialog("Déconnecté")
                    loadFeed()
                }
                .onFailure {
                    showInfoDialog("Erreur de déconnexion")
                }
        }
    }

    private fun handleAuthOrGenericError(error: Throwable) {
        if (error is IllegalStateException && error.message == "AUTH_REQUIRED") {
            showLoginDialog("Tu dois être connecté pour voter")
        } else {
            showInfoDialog("Impossible de voter")
        }
    }

    // ----------------------------
    // ✅ Dialog helpers
    // ----------------------------

    private fun showLoginDialog(message: String) {
        _uiState.update {
            it.copy(
                dialogMessage = message,
                dialogConfirmLabel = "Se connecter",
                dialogShouldOpenLogin = true
            )
        }
    }

    private fun showInfoDialog(message: String) {
        _uiState.update {
            it.copy(
                dialogMessage = message,
                dialogConfirmLabel = null, // juste "OK"
                dialogShouldOpenLogin = false
            )
        }
    }

    fun consumeDialog() {
        _uiState.update {
            it.copy(
                dialogMessage = null,
                dialogConfirmLabel = null,
                dialogShouldOpenLogin = false
            )
        }
    }

    fun clear() {
        job.cancel()
    }
}