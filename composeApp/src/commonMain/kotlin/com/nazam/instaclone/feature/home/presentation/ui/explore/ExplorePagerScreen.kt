package com.nazam.instaclone.feature.home.presentation.ui.explore

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import com.nazam.instaclone.feature.home.presentation.ui.VsPostItem

/**
 * ExplorePagerScreen :
 * - Swipe horizontal (gauche/droite)
 * - Uniquement les posts de la catégorie cliquée
 * - Démarre sur le post cliqué
 *
 * ✅ Réutilise VsPostItem => même rendu que le feed
 * ✅ Pas d’icônes
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExplorePagerScreen(
    ui: HomeUiState,
    contentPadding: PaddingValues,
    onBackClick: () -> Unit,
    onVoteLeft: (String) -> Unit,
    onVoteRight: (String) -> Unit,
    onOpenComments: (String) -> Unit
) {
    val categoryId = ExplorePagerStore.getCategoryId()
    val startPostId = ExplorePagerStore.getStartPostId()

    val postsInCategory =
        if (categoryId.isBlank()) ui.posts
        else ui.posts.filter { it.category == categoryId }

    val startIndex = postsInCategory.indexOfFirst { it.id == startPostId }.let { idx ->
        if (idx >= 0) idx else 0
    }

    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { postsInCategory.size }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050509))
            .padding(contentPadding)
    ) {
        // Petit “Retour” texte, sans icône
        Text(
            text = "Retour",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clickable { onBackClick() }
        )

        if (postsInCategory.isEmpty()) {
            Text(
                text = "Aucun post dans cette catégorie",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
            return
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { index ->
            val post = postsInCategory[index]

            val isVoting = ui.votingPostId == post.id

            VsPostItem(
                post = post,
                isVoting = isVoting,
                onVoteLeft = { onVoteLeft(post.id) },
                onVoteRight = { onVoteRight(post.id) },
                resultsAlpha = 1f,
                modifier = Modifier.fillMaxSize(),
                onCommentsClick = { onOpenComments(post.id) },
                onMessageClick = {},
                onShareClick = {},
                extraBottomPadding = 0.dp
            )
        }
    }
}