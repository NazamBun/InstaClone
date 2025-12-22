package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.Comment
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCreatePost: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToSignup: () -> Unit
) {
    val viewModel = remember { HomeViewModel() }
    val ui = viewModel.uiState.collectAsState()

    val pagerState = rememberPagerState(
        pageCount = { ui.value.posts.size }
    )

    val density = LocalDensity.current

    // ✅ hauteur du bloc bas (input/lock + bottom bar)
    var bottomBlockHeightDp by remember { mutableStateOf(0.dp) }

    // ✅ hauteur du panel (bottomSheet)
    val panelHeight: Dp = 320.dp

    // ✅ pousse les votes au-dessus du panel quand ouvert
    val extraVotesPadding: Dp =
        if (ui.value.isCommentsSheetOpen) panelHeight + bottomBlockHeightDp else 0.dp

    // ✅ POPUP
    if (ui.value.dialogMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.consumeDialog() },
            title = { Text(text = "Information") },
            text = { Text(text = ui.value.dialogMessage ?: "") },
            confirmButton = {
                val label = ui.value.dialogConfirmLabel
                if (label != null) {
                    TextButton(
                        onClick = {
                            val goLogin = ui.value.dialogShouldOpenLogin
                            viewModel.consumeDialog()
                            if (goLogin) onNavigateToLogin()
                        }
                    ) { Text(label) }
                } else {
                    TextButton(onClick = { viewModel.consumeDialog() }) { Text("OK") }
                }
            },
            dismissButton = {
                val second = ui.value.dialogSecondaryLabel
                if (second != null) {
                    TextButton(
                        onClick = {
                            val goSignup = ui.value.dialogShouldOpenSignup
                            viewModel.consumeDialog()
                            if (goSignup) onNavigateToSignup()
                        }
                    ) { Text(second) }

                    TextButton(onClick = { viewModel.consumeDialog() }) { Text("Annuler") }
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .onSizeChanged { size ->
                        bottomBlockHeightDp = with(density) { size.height.toDp() }
                    }
            ) {

                if (ui.value.isCommentsSheetOpen) {
                    if (ui.value.isLoggedIn) {
                        CommentInputBar(
                            letter = getUserLetter(
                                displayName = ui.value.currentUserDisplayName,
                                email = ui.value.currentUserEmail
                            ),
                            text = ui.value.newCommentText,
                            onTextChange = viewModel::onNewCommentChange,
                            onSend = viewModel::onSendCommentClicked
                        )
                    } else {
                        LockedCommentBar(
                            onClick = { viewModel.onCommentInputRequested() }
                        )
                    }
                }

                HomeBottomBar(
                    isLoggedIn = ui.value.isLoggedIn,
                    onCreatePostClick = {
                        if (ui.value.isLoggedIn) onNavigateToCreatePost()
                        else viewModel.onCreatePostClicked()
                    },
                    onLoginClick = onNavigateToLogin,
                    onLogoutClick = { viewModel.logout() }
                )
            }
        }
    ) { padding ->

        // ✅ Box plein écran sans padding
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF050509))
        ) {

            // ✅ FEED prend le padding du Scaffold
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when {
                    ui.value.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(0xFFFF4EB8)
                        )
                    }

                    ui.value.posts.isEmpty() -> {
                        Text(
                            text = "Aucun post",
                            color = Color.White,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    else -> {
                        VerticalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { index ->
                            val post = ui.value.posts[index]

                            val rawOffset =
                                (pagerState.currentPage - index) + pagerState.currentPageOffsetFraction
                            val pageOffset = abs(rawOffset)

                            val resultsAlpha = (1f - pageOffset * 1.5f).coerceIn(0f, 1f)
                            val isVoting = ui.value.votingPostId == post.id

                            VsPostItem(
                                post = post,
                                isVoting = isVoting,
                                onVoteLeft = { viewModel.voteLeft(post.id) },
                                onVoteRight = { viewModel.voteRight(post.id) },
                                resultsAlpha = resultsAlpha,
                                modifier = Modifier.fillMaxSize(),
                                onCommentsClick = { viewModel.openComments(post.id) },
                                onMessageClick = { },
                                onShareClick = { },
                                extraBottomPadding = extraVotesPadding
                            )
                        }
                    }
                }
            }

            // ✅ scrim au-dessus de tout
            if (ui.value.isCommentsSheetOpen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x88000000))
                        .clickable { viewModel.closeComments() }
                )
            }

            // ✅ panel collé au bloc du bas (0 espace)
            if (ui.value.isCommentsSheetOpen) {
                CommentsPanel(
                    bottomOffset = bottomBlockHeightDp,
                    height = panelHeight,
                    isLoading = ui.value.isCommentsLoading,
                    comments = ui.value.comments,
                    onClose = { viewModel.closeComments() }
                )
            }
        }
    }
}

private fun getUserLetter(displayName: String?, email: String?): String {
    val base = displayName?.trim().takeUnless { it.isNullOrBlank() }
        ?: email?.trim().takeUnless { it.isNullOrBlank() }
        ?: "?"
    return base.take(1).uppercase()
}

@Composable
private fun CommentsPanel(
    bottomOffset: Dp,
    height: Dp,
    isLoading: Boolean,
    comments: List<Comment>,
    onClose: () -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(comments.size) {
        if (comments.isNotEmpty()) listState.animateScrollToItem(comments.lastIndex)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomOffset)
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(height),
            color = Color(0xFF0B0B10),
            shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Commentaires",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Fermer",
                            tint = Color.White
                        )
                    }
                }

                Divider(color = Color(0x22FFFFFF))

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when {
                        isLoading -> CircularProgressIndicator(color = Color(0xFFFF4EB8))
                        comments.isEmpty() -> Text("Aucun commentaire pour l’instant.", color = Color(0xFFBBBBBB))
                        else -> {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 12.dp,
                                    bottom = 16.dp
                                )
                            ) {
                                items(items = comments, key = { it.id }) { c ->
                                    Text(
                                        text = "• ${c.content}",
                                        color = Color.White,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LockedCommentBar(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = Color(0xFF0B0B10)
    ) {
        Text(
            text = "Connecte-toi pour commenter",
            color = Color(0xFFBBBBBB),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )
    }
}

@Composable
private fun CommentInputBar(
    letter: String,
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFF0B0B10)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFFF4EB8), Color(0xFFFF9F3F))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = letter, color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.size(10.dp))

            TextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ajouter un commentaire...") },
                singleLine = true
            )

            Spacer(modifier = Modifier.size(8.dp))

            val canSend = text.trim().isNotEmpty()
            IconButton(onClick = onSend, enabled = canSend) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Envoyer",
                    tint = if (canSend) Color.White else Color(0x55FFFFFF)
                )
            }
        }
    }
}