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
                            posts = posts
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
        scope.launch {
            val result = voteLeftUseCase.execute(postId)
            result.onSuccess { updated ->
                _uiState.update {
                    it.copy(
                        posts = it.posts.map { post ->
                            if (post.id == updated.id) updated else post
                        }
                    )
                }
            }
        }
    }

    fun voteRight(postId: String) {
        scope.launch {
            val result = voteRightUseCase.execute(postId)
            result.onSuccess { updated ->
                _uiState.update {
                    it.copy(
                        posts = it.posts.map { post ->
                            if (post.id == updated.id) updated else post
                        }
                    )
                }
            }
        }
    }

    fun clear() {
        job.cancel()
    }
}