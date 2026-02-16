package com.nazam.instaclone.feature.home.presentation.viewmodel

import com.nazam.instaclone.feature.home.presentation.model.ExploreUiState
import com.nazam.instaclone.feature.home.presentation.ui.explore.ExploreSortMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel KMP simple pour Explore.
 * - Gère UNIQUEMENT le tri (V2)
 * ✅ Pas de dépendance Android
 * ✅ StateFlow propre
 */
class ExploreViewModel {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState

    fun onSortModeSelected(mode: ExploreSortMode) {
        _uiState.update { it.copy(sortMode = mode) }
    }
}