package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

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

            ui.value.posts.isEmpty() -> {
                Text(
                    text = "Aucun post pour le moment",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(ui.value.posts) { post ->
                        VsPostItem(
                            post = post,
                            onVoteLeft = { viewModel.voteLeft(post.id) },
                            onVoteRight = { viewModel.voteRight(post.id) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
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
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            // 🔸 Auteur + catégorie
            Text(
                text = post.authorName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = post.category,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🔸 Question
            Text(
                text = post.question,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔸 Zone VS avec vraies images
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                // Image gauche
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(MaterialTheme.shapes.medium)
                    ) {
                        KamelImage(
                            resource = asyncPainterResource(post.leftImageUrl),
                            contentDescription = post.leftLabel,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            onFailure = {
                                // fallback simple
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = post.leftLabel)
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${post.leftLabel} • ${post.leftVotesCount} votes",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                // Image droite
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(MaterialTheme.shapes.medium)
                    ) {
                        KamelImage(
                            resource = asyncPainterResource(post.rightImageUrl),
                            contentDescription = post.rightLabel,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            onFailure = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = post.rightLabel)
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${post.rightLabel} • ${post.rightVotesCount} votes",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Total : ${post.totalVotesCount} votes",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}