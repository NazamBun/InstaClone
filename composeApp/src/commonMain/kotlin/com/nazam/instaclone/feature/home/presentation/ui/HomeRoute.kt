package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.share.ShareLinkFactory
import com.nazam.instaclone.core.share.SharePayload
import com.nazam.instaclone.core.share.rememberShareLauncher
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeUiEvent
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.share_cta
import instaclone.composeapp.generated.resources.share_hook_a
import instaclone.composeapp.generated.resources.share_hook_b
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun HomeRoute(
    onNavigate: (Screen) -> Unit,
    contentPadding: PaddingValues
) {
    val viewModel: HomeViewModel = koinInject()
    val ui by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val shareLauncher = rememberShareLauncher()

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

    // Share (texte + lien)
    pendingSharePost?.let { post ->
        val link = ShareLinkFactory.postLink(post.id)

        // Petit "hook" qui change selon le post (stable, pas random)
        val hook = if (post.id.hashCode() % 2 == 0) {
            stringResource(Res.string.share_hook_a)
        } else {
            stringResource(Res.string.share_hook_b)
        }

        val cta = stringResource(Res.string.share_cta)

        val text = buildString {
            append(hook).append("\n\n")
            append(post.question.trim()).append("\n")
            append("A) ").append(post.leftLabel.trim()).append("\n")
            append("B) ").append(post.rightLabel.trim()).append("\n\n")
            append(cta).append("\n")
            append(link)
        }

        LaunchedEffect(post.id) {
            shareLauncher.share(SharePayload(text = text, subject = "VS"))
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
