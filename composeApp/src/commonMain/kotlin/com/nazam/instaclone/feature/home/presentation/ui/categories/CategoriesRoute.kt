package com.nazam.instaclone.feature.home.presentation.ui.categories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nazam.instaclone.core.navigation.Screen
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CategoriesRoute(
    onNavigate: (Screen) -> Unit
) {
    val viewModel: CategoriesViewModel = koinViewModel()
    val ui by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is CategoriesUiEvent.NavigateBack -> onNavigate(event.screen)
            }
        }
    }

    CategoriesScreen(
        ui = ui,
        onCategoryClick = viewModel::onCategoryClicked
    )
}
