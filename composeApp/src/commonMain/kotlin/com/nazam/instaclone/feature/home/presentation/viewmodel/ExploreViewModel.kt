package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.feature.home.domain.usecase.GetExplorePostsUseCase
import com.nazam.instaclone.feature.home.presentation.model.ExploreUiState
import com.nazam.instaclone.feature.home.presentation.ui.explore.ExploreSortMode
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.explore_load_error
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExploreViewModel(
    private val dispatchers: AppDispatchers,
    private val getExplorePostsUseCase: GetExplorePostsUseCase
) {
    private companion object {
        const val PAGE_SIZE = 30
    }

    private val job = SupervisorJob()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState

    init {
        load()
    }

    fun load() = loadInternal(reset = true)

    fun loadMore() = loadInternal(reset = false)

    fun onSortModeSelected(mode: ExploreSortMode) {
        _uiState.update { it.copy(sortMode = mode) }
    }

    fun clear() = job.cancel()

    private fun loadInternal(reset: Boolean) {
        val state = _uiState.value

        if (reset && state.isLoading) return
        if (!reset && (state.isLoading || state.isLoadingMore || state.endReached)) return

        val offset = if (reset) 0 else state.posts.size

        scope.launch {
            _uiState.update {
                if (reset) {
                    it.copy(
                        isLoading = true,
                        isLoadingMore = false,
                        endReached = false,
                        error = null
                    )
                } else {
                    it.copy(
                        isLoadingMore = true,
                        error = null
                    )
                }
            }

            val result = withContext(dispatchers.default) {
                getExplorePostsUseCase.execute(offset = offset, limit = PAGE_SIZE)
            }

            result.onSuccess { page ->
                _uiState.update { current ->
                    val merged = if (reset) page else (current.posts + page).distinctBy { it.id }
                    current.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        endReached = page.size < PAGE_SIZE,
                        posts = merged,
                        error = null
                    )
                }
            }.onFailure {
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        posts = if (reset) emptyList() else current.posts,
                        error = UiText.Resource(Res.string.explore_load_error)
                    )
                }
            }
        }
    }
}
