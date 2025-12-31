package com.nazam.instaclone.feature.home.presentation.ui.categories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nazam.instaclone.core.navigation.Screen
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

@Composable
fun CategoriesRoute(
    onNavigate: (Screen) -> Unit
) {
    val viewModel: CategoriesViewModel = koinInject()
    val ui by viewModel.uiState.collectAsState()

    DisposableEffect(Unit) {
        onDispose { viewModel.clear() }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                CategoriesUiEvent.NavigateBackToCreatePost -> onNavigate(Screen.CreatePost)
            }
        }
    }

    CategoriesScreen(
        ui = ui,
        onCategoryClick = viewModel::onCategoryClicked
    )
}