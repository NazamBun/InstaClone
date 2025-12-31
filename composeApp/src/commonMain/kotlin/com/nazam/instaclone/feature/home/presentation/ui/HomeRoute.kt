package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.material3.SnackbarHostState
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
 * Route = colle l'UI au ViewModel.
 * Elle écoute les events (navigation + messages one-shot).
 */
@Composable
fun HomeRoute(
    onNavigate: (Screen) -> Unit
) {
    val viewModel: HomeViewModel = koinInject()
    val ui by viewModel.uiState.collectAsState()

    val snackbarHostState = SnackbarHostState()

    DisposableEffect(Unit) {
        onDispose { viewModel.clear() }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is HomeUiEvent.Navigate -> onNavigate(event.screen)
                is HomeUiEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    HomeScreen(
        ui = ui,
        snackbarHostState = snackbarHostState,

        onCreatePostClick = viewModel::onCreatePostClicked,
        onLoginClick = viewModel::onLoginClicked,
        onLogoutClick = viewModel::logout,

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