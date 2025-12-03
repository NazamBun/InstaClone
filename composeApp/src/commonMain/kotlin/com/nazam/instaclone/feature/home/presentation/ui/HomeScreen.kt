package com.nazam.instaclone.feature.home.presentation.ui
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel

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
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(ui.value.posts) { post ->
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
    // 🔥 ICI on fera le design "burger vs salade" plus tard
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = post.authorName)
        Text(text = post.question, modifier = Modifier.padding(top = 8.dp))
        Text(text = "Gauche : ${post.leftLabel} (${post.leftVotesCount})")
        Text(text = "Droite : ${post.rightLabel} (${post.rightVotesCount})")
    }
}