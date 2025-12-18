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

    private fun refreshSession() {
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
                            isLoading = false,
                            snackbarMessage = "Erreur de chargement",
                            snackbarActionLabel = null,
                            shouldOpenLogin = false
                        )
                    }
                }
        }
    }

    // ✅ Étape 2 : clic sur "Créer"
    fun onCreatePostClicked() {
        if (_uiState.value.isLoggedIn) {
            _uiState.update { it.copy(shouldNavigateToCreatePost = true) }
        } else {
            _uiState.update {
                it.copy(
                    snackbarMessage = "Tu dois être connecté pour créer un post",
                    snackbarActionLabel = "Se connecter",
                    shouldOpenLogin = true
                )
            }
        }
    }

    fun consumeNavigateToCreatePost() {
        _uiState.update { it.copy(shouldNavigateToCreatePost = false) }
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
                    handleVoteError(error)
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
                    handleVoteError(error)
                }
        }
    }

    private fun handleVoteError(error: Throwable) {
        if (error is IllegalStateException && error.message == "AUTH_REQUIRED") {
            _uiState.update {
                it.copy(
                    votingPostId = null,
                    snackbarMessage = "Tu dois être connecté pour voter",
                    snackbarActionLabel = "Se connecter",
                    shouldOpenLogin = true
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    votingPostId = null,
                    snackbarMessage = "Impossible de voter",
                    snackbarActionLabel = null,
                    shouldOpenLogin = false
                )
            }
        }
    }

    fun logout() {
        scope.launch {
            val result = logoutUseCase.execute()

            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoggedIn = false,
                            snackbarMessage = "Déconnecté",
                            snackbarActionLabel = null,
                            shouldOpenLogin = false,
                            shouldNavigateToLogin = true
                        )
                    }
                    loadFeed()
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            snackbarMessage = "Erreur de déconnexion",
                            snackbarActionLabel = null,
                            shouldOpenLogin = false
                        )
                    }
                }
        }
    }

    fun consumeSnackbar() {
        _uiState.update {
            it.copy(
                snackbarMessage = null,
                snackbarActionLabel = null,
                shouldOpenLogin = false
            )
        }
    }

    fun consumeNavigateToLogin() {
        _uiState.update { it.copy(shouldNavigateToLogin = false) }
    }

    fun clear() {
        job.cancel()
    }
}