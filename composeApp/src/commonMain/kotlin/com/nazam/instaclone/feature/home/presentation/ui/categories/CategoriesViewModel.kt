package com.nazam.instaclone.feature.home.presentation.ui.categories

import androidx.lifecycle.ViewModel
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.home.domain.model.VoteCategories
import com.nazam.instaclone.feature.home.domain.model.VoteCategory
import com.nazam.instaclone.feature.home.presentation.categories.CategorySelectionStore
import com.nazam.instaclone.feature.home.presentation.categories.HomeFilterStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoriesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState(items = VoteCategories.all))
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CategoriesUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<CategoriesUiEvent> = _events.asSharedFlow()

    fun onCategoryClicked(category: VoteCategory) {
        when (CategorySelectionStore.getTarget()) {
            CategorySelectionStore.Target.CREATE_POST ->
                _events.tryEmit(CategoriesUiEvent.NavigateBack(Screen.CreatePost))
            CategorySelectionStore.Target.HOME_FILTER -> {
                HomeFilterStore.setCategory(category.id)
                _events.tryEmit(CategoriesUiEvent.NavigateBack(Screen.Home))
            }
        }
    }
}
