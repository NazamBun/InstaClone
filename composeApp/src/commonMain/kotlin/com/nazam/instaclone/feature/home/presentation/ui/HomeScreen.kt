package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState

@Composable
fun HomeScreen() {

    val viewModel = remember { HomeViewModel() }
    val ui = viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        when {
            ui.value.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            ui.value.errorMessage != null -> {
                Text(
                    text = ui.value.errorMessage!!,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(ui.value.posts.size) { index ->
                        val post = ui.value.posts[index]
                        VsPostItem(
                            post = post,
                            onVoteLeft = { viewModel.voteLeft(post.id) },
                            onVoteRight = { viewModel.voteRight(post.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VsPostItem(
    post: VsPost,
    onVoteLeft: () -> Unit,
    onVoteRight: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {

            // 👤 Ligne auteur
            Text(
                text = "${post.authorName} • ${post.category}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // 🖼️ Les deux images côte à côte
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                // Image gauche
                AsyncImage(
                    model = post.leftImageUrl,
                    contentDescription = post.leftLabel,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onVoteLeft() }
                )

                // Image droite
                AsyncImage(
                    model = post.rightImageUrl,
                    contentDescription = post.rightLabel,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onVoteRight() }
                )
            }

            // ❓ Question
            Text(
                text = post.question,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // 📊 Résumé votes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${post.leftLabel} (${post.leftVotesCount})",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${post.rightLabel} (${post.rightVotesCount})",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "${post.totalVotesCount} votes au total",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}