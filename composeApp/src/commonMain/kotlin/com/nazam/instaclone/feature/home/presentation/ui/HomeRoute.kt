package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel
import org.koin.compose.koinInject

@Composable
fun HomeRoute(
    onNavigateToCreatePost: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToSignup: () -> Unit
) {
    val viewModel: HomeViewModel = koinInject()

    DisposableEffect(viewModel) {
        onDispose(viewModel::clear)
    }

    val ui by viewModel.uiState.collectAsState()

    HomeScreen(
        ui = ui,
        onNavigateToCreatePost = onNavigateToCreatePost,
        onNavigateToLogin = onNavigateToLogin,
        onNavigateToSignup = onNavigateToSignup,
        onCreatePostClick = viewModel::onCreatePostClicked,
        onLogoutClick = viewModel::logout,
        onVoteLeft = viewModel::voteLeft,
        onVoteRight = viewModel::voteRight,
        onOpenComments = viewModel::openComments,
        onCloseComments = viewModel::closeComments,
        onNewCommentChange = viewModel::onNewCommentChange,
        onSendCommentClick = viewModel::onSendCommentClicked,
        onCommentInputRequested = viewModel::onCommentInputRequested,
        onConsumeDialog = viewModel::consumeDialog
    )
}