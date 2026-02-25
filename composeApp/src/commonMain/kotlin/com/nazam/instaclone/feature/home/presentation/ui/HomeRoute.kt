package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.share.SharePayload
import com.nazam.instaclone.core.share.rememberShareCardRenderer
import com.nazam.instaclone.core.share.rememberShareLauncher
import com.nazam.instaclone.core.share.ViralShareTextFactory
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeUiEvent
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

/**
 * Route = colle l'UI au ViewModel.
 * - UiText -> String uniquement dans la composition (KMP friendly)
 * - Partage natif (Android/iOS) : image + texte + lien
 */
@Composable
fun HomeRoute(
    onNavigate: (Screen) -> Unit,
    contentPadding: PaddingValues
) {
    val viewModel: HomeViewModel = koinInject()
    val ui by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val shareLauncher = rememberShareLauncher()
    val shareCardRenderer = rememberShareCardRenderer()

    var pendingMessage by remember { mutableStateOf<UiText?>(null) }
    var pendingSharePost by remember { mutableStateOf<VsPost?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is HomeUiEvent.Navigate -> onNavigate(event.screen)
                is HomeUiEvent.ShowMessage -> pendingMessage = event.message
                is HomeUiEvent.Share -> pendingSharePost = event.post
            }
        }
    }

    // Snackbar
    pendingMessage?.let { msg ->
        val text = msg.asString()
        LaunchedEffect(text) {
            snackbarHostState.showSnackbar(text)
            pendingMessage = null
        }
    }

    // Share natif (image + texte)
    pendingSharePost?.let { post ->
        val payloadText = ViralShareTextFactory.fromPost(post).text
        val png = shareCardRenderer.renderPng(post)

        LaunchedEffect(post.id) {
            shareLauncher.share(
                SharePayload(
                    text = payloadText,
                    subject = "VS",
                    imagePng = png,
                    imageFileName = "vs_${post.id}.png"
                )
            )
            pendingSharePost = null
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
        onShare = viewModel::onShareClicked,

        onNewCommentChange = viewModel::onNewCommentChange,
        onSendCommentClick = viewModel::onSendCommentClicked,
        onCommentInputRequested = viewModel::onCommentInputRequested,

        onConsumeDialog = viewModel::consumeDialog,
        onDialogConfirm = viewModel::onDialogConfirmClicked,
        onDialogSecondary = viewModel::onDialogSecondaryClicked
    )
}
