package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    onNavigateToCreatePost: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToSignup: () -> Unit
) {
    val viewModel = remember { HomeViewModel() }

    DisposableEffect(Unit) {
        onDispose { viewModel.clear() }
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