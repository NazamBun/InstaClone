package com.nazam.instaclone.feature.home.presentation.ui.categories

import com.nazam.instaclone.feature.home.domain.model.VoteCategory
import com.nazam.instaclone.feature.home.domain.model.VoteCategories
import com.nazam.instaclone.feature.home.presentation.draft.CreatePostDraftStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class CategoriesViewModel {

    private val job = Job()
    private val scope = CoroutineScope(job)

    private val _uiState = MutableStateFlow(CategoriesUiState(items = VoteCategories.all))
    val uiState: StateFlow<CategoriesUiState> = _uiState

    private val _events = MutableSharedFlow<CategoriesUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<CategoriesUiEvent> = _events

    fun onCategoryClicked(category: VoteCategory) {
        val current = CreatePostDraftStore.get()
        CreatePostDraftStore.update(current.copy(categoryId = category.id))
        _events.tryEmit(CategoriesUiEvent.NavigateBackToCreatePost)
    }

    fun clear() {
        job.cancel()
    }
}