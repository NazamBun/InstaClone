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
import com.nazam.instaclone.core.clipboard.rememberClipboardManager
import com.nazam.instaclone.core.navigation.Screen
import com.nazam.instaclone.core.share.SharePayload
import com.nazam.instaclone.core.share.ViralShareTextFactory
import com.nazam.instaclone.core.share.rememberShareCardRenderer
import com.nazam.instaclone.core.share.rememberShareLauncher
import com.nazam.instaclone.core.ui.UiText
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.ui.components.share.ShareBottomSheet
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeUiEvent
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel
import com.nazam.instaclone.feature.profile.presentation.navigation.ProfileTargetStore
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.share_copied
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeRoute(
    onNavigate: (Screen) -> Unit,
    contentPadding: PaddingValues
) {
    val viewModel: HomeViewModel = koinViewModel()
    val ui by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val shareLauncher = rememberShareLauncher()
    val shareCardRenderer = rememberShareCardRenderer()
    val clipboard = rememberClipboardManager()

    var pendingMessage by remember { mutableStateOf<UiText?>(null) }
    var shareSheetPost by remember { mutableStateOf<VsPost?>(null) }
    var pendingSnackText by remember { mutableStateOf<String?>(null) }

    val copiedLabel = stringResource(Res.string.share_copied)
    val pendingMessageText = pendingMessage?.asString()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is HomeUiEvent.Navigate -> onNavigate(event.screen)
                is HomeUiEvent.ShowMessage -> pendingMessage = event.message
                is HomeUiEvent.Share -> shareSheetPost = event.post
            }
        }
    }

    LaunchedEffect(pendingMessageText) {
        pendingMessageText?.let {
            snackbarHostState.showSnackbar(it)
            pendingMessage = null
        }
    }

    LaunchedEffect(pendingSnackText) {
        pendingSnackText?.let {
            snackbarHostState.showSnackbar(it)
            pendingSnackText = null
        }
    }

    shareSheetPost?.let { post ->
        val content = ViralShareTextFactory.contentFromPost(post)
        val png = shareCardRenderer.renderPng(post)
        val payload: SharePayload = content.payload.copy(
            imagePng = png.takeIf { it.isNotEmpty() },
            imageFileName = "vs_${post.id}.png"
        )

        ShareBottomSheet(
            previewTitle = post.question.trim(),
            onDismiss = { shareSheetPost = null },
            onShare = { shareLauncher.share(payload); shareSheetPost = null },
            onCopyLink = { clipboard.setText(content.link); pendingSnackText = copiedLabel },
            onCopyText = { clipboard.setText(payload.text); pendingSnackText = copiedLabel }
        )
    }

    HomeScreen(
        ui = ui,
        snackbarHostState = snackbarHostState,
        contentPadding = contentPadding,
        onCreatePostClick = viewModel::onCreatePostClicked,
        onFeedModeSelected = viewModel::onFeedModeSelected,
        onVoteLeft = viewModel::voteLeft,
        onVoteRight = viewModel::voteRight,
        onOpenComments = viewModel::openComments,
        onCloseComments = viewModel::closeComments,
        onOpenAuthor = { post ->
            val authorId = post.authorId ?: return@HomeScreen
            ProfileTargetStore.open(authorId, post.authorName, Screen.Home)
            onNavigate(Screen.UserProfile)
        },
        onShare = viewModel::onShareClicked,
        onNewCommentChange = viewModel::onNewCommentChange,
        onSendCommentClick = viewModel::onSendCommentClicked,
        onCommentInputRequested = viewModel::onCommentInputRequested,
        onConsumeDialog = viewModel::consumeDialog,
        onDialogConfirm = viewModel::onDialogConfirmClicked,
        onDialogSecondary = viewModel::onDialogSecondaryClicked,
        onLoadMore = viewModel::loadMore
    )
}
