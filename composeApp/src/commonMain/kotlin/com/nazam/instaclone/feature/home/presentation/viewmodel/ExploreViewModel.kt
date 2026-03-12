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
    private val job = SupervisorJob()
    private val scope = CoroutineScope(dispatchers.main + job)

    private val _uiState = MutableStateFlow(ExploreUiState(isLoading = true))
    val uiState: StateFlow<ExploreUiState> = _uiState

    init { load() }

    fun load() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = withContext(dispatchers.default) {
                getExplorePostsUseCase.execute()
            }

            result.onSuccess { posts ->
                _uiState.update {
                    it.copy(isLoading = false, posts = posts, error = null)
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        posts = emptyList(),
                        error = UiText.Resource(Res.string.explore_load_error)
                    )
                }
            }
        }
    }

    fun onSortModeSelected(mode: ExploreSortMode) {
        _uiState.update { it.copy(sortMode = mode) }
    }

    fun clear() = job.cancel()
}
