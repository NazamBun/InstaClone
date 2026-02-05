package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.padding
import com.nazam.instaclone.core.media.rememberImagePicker
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.home.presentation.viewmodel.CreatePostUiEvent
import com.nazam.instaclone.feature.home.presentation.viewmodel.CreatePostViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

@Composable
fun CreatePostRoute(
    onNavigate: (Screen) -> Unit,
) {
    val viewModel: CreatePostViewModel = koinInject()
    val ui by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // On stocke le message UiText ici (pour le convertir en String dans la composition)
    var pendingMessage by remember { mutableStateOf<UiText?>(null) }

    val pickLeftImage = rememberImagePicker(onImagePicked = viewModel::onLeftImageSelected)
    val pickRightImage = rememberImagePicker(onImagePicked = viewModel::onRightImageSelected)

    DisposableEffect(Unit) {
        onDispose { viewModel.clear() }
    }

    LaunchedEffect(Unit) {
        viewModel.checkAccess()
        viewModel.refreshFromDraft()

        viewModel.events.collectLatest { event ->
            when (event) {
                CreatePostUiEvent.PostCreated -> onNavigate(Screen.Home)
                CreatePostUiEvent.NavigateBack -> onNavigate(Screen.Home)
                CreatePostUiEvent.NavigateToLogin -> onNavigate(Screen.Login)
                CreatePostUiEvent.NavigateToCategories -> onNavigate(Screen.Categories)
                is CreatePostUiEvent.ShowMessage -> pendingMessage = event.message
            }
        }
    }

    // ✅ Conversion UiText -> String dans la composition (safe)
    val messageText: String? = pendingMessage?.asString()

    LaunchedEffect(messageText) {
        if (messageText != null) {
            snackbarHostState.showSnackbar(messageText)
            pendingMessage = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        CreatePostScreen(
            ui = ui,
            onQuestionChange = viewModel::onQuestionChange,
            onLeftLabelChange = viewModel::onLeftLabelChange,
            onRightLabelChange = viewModel::onRightLabelChange,
            onPickLeftImageClick = pickLeftImage,
            onPickRightImageClick = pickRightImage,
            onChooseCategoryClick = viewModel::onChooseCategoryClicked,
            onSubmitClick = viewModel::submitPost,
            onCancelClick = viewModel::onCancelClicked,
            modifier = Modifier.padding(padding)
        )
    }
}