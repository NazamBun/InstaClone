package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.feature.home.presentation.viewmodel.CreatePostUiEvent
import com.nazam.instaclone.feature.home.presentation.viewmodel.CreatePostViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

@Composable
fun CreatePostRoute(
    onNavigate: (Screen) -> Unit
) {
    val viewModel: CreatePostViewModel = koinInject()
    val ui by viewModel.uiState.collectAsState()

    DisposableEffect(Unit) {
        onDispose { viewModel.clear() }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                CreatePostUiEvent.PostCreated -> onNavigate(Screen.Home)
                CreatePostUiEvent.NavigateBack -> onNavigate(Screen.Home)
                is CreatePostUiEvent.ShowMessage -> Unit
            }
        }
    }

    CreatePostScreen(
        ui = ui,
        onQuestionChange = viewModel::onQuestionChange,
        onLeftLabelChange = viewModel::onLeftLabelChange,
        onRightLabelChange = viewModel::onRightLabelChange,
        onLeftImageUrlChange = viewModel::onLeftImageUrlChange,
        onRightImageUrlChange = viewModel::onRightImageUrlChange,
        onCategoryChange = viewModel::onCategoryChange,
        onSubmitClick = viewModel::submitPost,
        onCancelClick = viewModel::onCancelClicked
    )
}