package com.nazam.instaclone.feature.home.presentation.ui.createpostyesno

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
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.media.rememberImagePicker
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.home.presentation.viewmodel.CreatePostYesNoUiEvent
import com.nazam.instaclone.feature.home.presentation.viewmodel.CreatePostYesNoViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreatePostYesNoRoute(
    onNavigate: (Screen) -> Unit
) {
    val viewModel: CreatePostYesNoViewModel = koinViewModel()
    val ui by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var pendingMessage by remember { mutableStateOf<UiText?>(null) }

    val pickImage = rememberImagePicker(onImagePicked = viewModel::onImageSelected)
    val pendingMessageText = pendingMessage?.asString()

    LaunchedEffect(Unit) {
        viewModel.checkAccess()
        viewModel.events.collectLatest { event ->
            when (event) {
                CreatePostYesNoUiEvent.PostCreated -> onNavigate(Screen.Home)
                CreatePostYesNoUiEvent.NavigateBack -> onNavigate(Screen.CreatePostType)
                CreatePostYesNoUiEvent.NavigateToLogin -> onNavigate(Screen.Login)
                is CreatePostYesNoUiEvent.ShowMessage -> pendingMessage = event.message
            }
        }
    }

    LaunchedEffect(pendingMessageText) {
        val text = pendingMessageText ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(text)
        pendingMessage = null
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 80.dp),
            )
        }
    ) { padding ->
        CreatePostYesNoScreen(
            ui = ui,
            onBackClick = viewModel::onCancelClicked,
            onQuestionChange = viewModel::onQuestionChange,
            onPickImageClick = pickImage,
            onSubmitClick = viewModel::submitPost,
            modifier = Modifier.padding(padding)
        )
    }
}
