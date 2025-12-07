package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen() {

    val viewModel = remember { HomeViewModel() }
    val uiState = viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        when {
            uiState.value.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.value.errorMessage != null -> {
                Text(
                    text = uiState.value.errorMessage!!,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                // Petit debug : afficher combien de posts on a
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(text = "Posts : ${uiState.value.posts.size}")

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.value.posts) { post ->
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
}

@Composable
fun VsPostItem(
    post: VsPost,
    onVoteLeft: () -> Unit,
    onVoteRight: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Auteur
        Text(text = post.authorName)

        // Question
        Text(
            text = post.question,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Ligne des deux choix
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Bouton gauche
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Button(
                    onClick = onVoteLeft,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = post.leftLabel)
                }
                Text(
                    text = "Votes gauche : ${post.leftVotesCount}",
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Bouton droite
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Button(
                    onClick = onVoteRight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = post.rightLabel)
                }
                Text(
                    text = "Votes droite : ${post.rightVotesCount}",
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}