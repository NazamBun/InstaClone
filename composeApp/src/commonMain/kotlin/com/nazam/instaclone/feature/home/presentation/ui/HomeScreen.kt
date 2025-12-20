package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
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

    // ✅ POPUP (Dialog)
    if (ui.value.dialogMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.consumeDialog() },
            title = { Text(text = "Information") },
            text = { Text(text = ui.value.dialogMessage ?: "") },

            // bouton principal (Se connecter)
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

            // bouton secondaire (Créer un compte) + Annuler
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

                    TextButton(onClick = { viewModel.consumeDialog() }) {
                        Text("Annuler")
                    }
                }
            }
        )
    }

    // ✅ BottomSheet commentaires (VRAIE UI) — sans windowInsets
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    if (ui.value.isCommentsSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeComments() },
            sheetState = sheetState
        ) {
            CommentsSheetContent(
                isLoading = ui.value.isCommentsLoading,
                comments = ui.value.comments,
                newText = ui.value.newCommentText,
                isLoggedIn = ui.value.isLoggedIn,
                onTextChange = viewModel::onNewCommentChange,
                onSendClick = viewModel::submitComment
            )
        }
    }

    Scaffold(
        bottomBar = {
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
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF050509))
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
                            onMessageClick = { /* plus tard */ },
                            onShareClick = { /* plus tard */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentsSheetContent(
    isLoading: Boolean,
    comments: List<Comment>,
    newText: String,
    isLoggedIn: Boolean,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Commentaires",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )
        Divider()

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(color = Color(0xFFFF4EB8))
                }

                comments.isEmpty() -> {
                    Text(text = "Aucun commentaire pour l’instant.")
                }

                else -> {
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            count = comments.size,
                            key = { index -> comments[index].id }
                        ) { index ->
                            CommentRow(comment = comments[index])
                        }

                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }

        Divider()

        val canSend = isLoggedIn && !isLoading && newText.trim().isNotEmpty()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newText,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = if (isLoggedIn) "Écris un commentaire…" else "Connecte-toi pour commenter…"
                    )
                },
                singleLine = true,
                enabled = isLoggedIn && !isLoading
            )

            Spacer(modifier = Modifier.size(10.dp))

            IconButton(
                onClick = onSendClick,
                enabled = canSend
            ) {
                Icon(
                    imageVector = Icons.Outlined.Send,
                    contentDescription = "Envoyer"
                )
            }
        }
    }
}

@Composable
private fun CommentRow(comment: Comment) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Avatar(
            url = comment.authorAvatarUrl,
            fallbackText = comment.authorName ?: "?"
        )

        Spacer(modifier = Modifier.size(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = comment.authorName ?: "Inconnu",
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = comment.content,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun Avatar(
    url: String?,
    fallbackText: String
) {
    val size = 36.dp

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFF4EB8), Color(0xFFFF9F3F))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!url.isNullOrBlank()) {
            AsyncImage(
                model = url,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
            )
        } else {
            Text(
                text = fallbackText.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}