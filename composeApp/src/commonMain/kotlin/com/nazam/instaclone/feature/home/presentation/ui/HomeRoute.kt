package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeUiEvent
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

/**
 * Route = colle l'UI au ViewModel.
 * ✅ UiText -> String uniquement dans la composition (KMP friendly)
 */
@Composable
fun HomeRoute(
    onNavigate: (Screen) -> Unit,
    contentPadding: PaddingValues
) {
    val viewModel: HomeViewModel = koinInject()
    val ui by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // ✅ On stocke le message UiText ici (pour le convertir en String dans la composition)
    var pendingMessage by remember { mutableStateOf<UiText?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is HomeUiEvent.Navigate -> onNavigate(event.screen)
                is HomeUiEvent.ShowMessage -> pendingMessage = event.message
            }
        }
    }

    // ✅ Conversion UiText -> String dans la composition (safe)
    pendingMessage?.let { msg ->
        val text = msg.asString()
        LaunchedEffect(text) {
            snackbarHostState.showSnackbar(text)
            pendingMessage = null
        }
    }

    HomeScreen(
        ui = ui,
        snackbarHostState = snackbarHostState,
        contentPadding = contentPadding,

        onCreatePostClick = viewModel::onCreatePostClicked,

        onVoteLeft = viewModel::voteLeft,
        onVoteRight = viewModel::voteRight,
        onOpenComments = viewModel::openComments,
        onCloseComments = viewModel::closeComments,

        onNewCommentChange = viewModel::onNewCommentChange,
        onSendCommentClick = viewModel::onSendCommentClicked,
        onCommentInputRequested = viewModel::onCommentInputRequested,

        onConsumeDialog = viewModel::consumeDialog,
        onDialogConfirm = viewModel::onDialogConfirmClicked,
        onDialogSecondary = viewModel::onDialogSecondaryClicked
    )
}