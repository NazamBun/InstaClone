package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.nazam.instaclone.core.media.rememberImagePicker
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.home.presentation.viewmodel.CreatePostUiEvent
import com.nazam.instaclone.feature.home.presentation.viewmodel.CreatePostViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreatePostRoute(
    onNavigate: (Screen) -> Unit
) {
    val viewModel: CreatePostViewModel = koinViewModel()
    val ui by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var pendingMessage by remember { mutableStateOf<UiText?>(null) }

    val pickLeftImage = rememberImagePicker(onImagePicked = viewModel::onLeftImageSelected)
    val pickRightImage = rememberImagePicker(onImagePicked = viewModel::onRightImageSelected)
    val pendingMessageText = pendingMessage?.asString()

    LaunchedEffect(Unit) {
        viewModel.checkAccess()
        viewModel.refreshFromDraft()
        viewModel.events.collectLatest { event ->
            when (event) {
                CreatePostUiEvent.PostCreated -> onNavigate(Screen.Home)
                CreatePostUiEvent.NavigateBack -> onNavigate(Screen.Home)
                CreatePostUiEvent.NavigateToLogin -> onNavigate(Screen.Login)
                is CreatePostUiEvent.ShowMessage -> pendingMessage = event.message
            }
        }
    }

    LaunchedEffect(pendingMessageText) {
        val text = pendingMessageText ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(text)
        pendingMessage = null
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        CreatePostScreen(
            ui = ui,
            onQuestionChange = viewModel::onQuestionChange,
            onLeftLabelChange = viewModel::onLeftLabelChange,
            onRightLabelChange = viewModel::onRightLabelChange,
            onVsBadgeSelected = viewModel::onVsBadgeSelected,
            onPickLeftImageClick = pickLeftImage,
            onPickRightImageClick = pickRightImage,
            onSubmitClick = viewModel::submitPost,
            onCancelClick = viewModel::onCancelClicked,
            modifier = Modifier.padding(padding)
        )
    }
}
