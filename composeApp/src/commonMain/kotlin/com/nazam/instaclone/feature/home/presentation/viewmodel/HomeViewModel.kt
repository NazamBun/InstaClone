package com.nazam.instaclone.feature.home.presentation.viewmodel

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

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadFeed()
    }

    fun loadFeed() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = getFeedUseCase.execute()

            result
                .onSuccess { posts ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            posts = posts,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erreur de chargement"
                        )
                    }
                }
        }
    }

    fun voteLeft(postId: String) {
        // 🔒 si CE post est déjà en train de voter -> stop
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
                            },
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    val message =
                        if (error is IllegalStateException && error.message == "AUTH_REQUIRED") {
                            "Tu dois être connecté pour voter."
                        } else {
                            "Impossible de voter pour le moment."
                        }

                    _uiState.update { state ->
                        state.copy(
                            votingPostId = null,
                            errorMessage = message
                        )
                    }
                }
        }
    }

    fun voteRight(postId: String) {
        // 🔒 si CE post est déjà en train de voter -> stop
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
                            },
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    val message =
                        if (error is IllegalStateException && error.message == "AUTH_REQUIRED") {
                            "Tu dois être connecté pour voter."
                        } else {
                            "Impossible de voter pour le moment."
                        }

                    _uiState.update { state ->
                        state.copy(
                            votingPostId = null,
                            errorMessage = message
                        )
                    }
                }
        }
    }

    fun clear() {
        job.cancel()
    }
}