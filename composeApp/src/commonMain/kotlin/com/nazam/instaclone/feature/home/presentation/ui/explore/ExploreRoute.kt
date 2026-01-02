package com.nazam.instaclone.feature.home.presentation.ui.explore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeUiEvent
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

/**
 * ExploreRoute :
 * - on réutilise HomeViewModel pour éviter de créer 10 ViewModels
 * - on garde un seul endroit qui charge le feed (pro et simple)
 */
@Composable
fun ExploreRoute(
    onNavigate: (Screen) -> Unit
) {
    val viewModel: HomeViewModel = koinInject()
    val ui by viewModel.uiState.collectAsState()

    DisposableEffect(Unit) {
        onDispose { viewModel.clear() }
    }

    LaunchedEffect(Unit) {
        // On s'assure que le filtre est chargé depuis HomeFilterStore
        viewModel.refreshFilter()

        viewModel.events.collectLatest { event ->
            when (event) {
                is HomeUiEvent.Navigate -> onNavigate(event.screen)
                is HomeUiEvent.ShowMessage -> {
                    // Explore n'affiche pas de snackbar pour l'instant (simple)
                }
            }
        }
    }

    ExploreScreen(
        ui = ui,
        onBackHome = { onNavigate(Screen.Home) },
        onCategoryClick = viewModel::onExploreCategoryClicked,
        onClearCategory = viewModel::onExploreClearCategory
    )
}