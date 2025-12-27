package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.ui.components.vspost.VsPostActionRail
import com.nazam.instaclone.feature.home.presentation.ui.components.vspost.VsPostHeader
import com.nazam.instaclone.feature.home.presentation.ui.components.vspost.VsPostOverlayGradient
import com.nazam.instaclone.feature.home.presentation.ui.components.vspost.VsPostQuestionCard
import com.nazam.instaclone.feature.home.presentation.ui.components.vspost.VsPostResults
import com.nazam.instaclone.feature.home.presentation.ui.components.vspost.VsPostVoteImages
import com.nazam.instaclone.feature.home.presentation.ui.components.vspost.VsPostVotingOverlay
import com.nazam.instaclone.feature.home.presentation.ui.components.vspost.VsPostVsBadge

@Composable
fun VsPostItem(
    post: VsPost,
    isVoting: Boolean,
    onVoteLeft: () -> Unit,
    onVoteRight: () -> Unit,
    resultsAlpha: Float,
    modifier: Modifier = Modifier,
    onCommentsClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    extraBottomPadding: Dp = 0.dp
) {
    Box(modifier = modifier.fillMaxSize()) {

        VsPostVoteImages(
            post = post,
            isVoting = isVoting,
            onVoteLeft = onVoteLeft,
            onVoteRight = onVoteRight
        )

        if (isVoting) {
            VsPostVotingOverlay()
        }

        VsPostOverlayGradient()

        VsPostHeader(
            authorName = post.authorName,
            category = post.category,
            modifier = Modifier.align(Alignment.TopStart)
        )

        VsPostActionRail(
            modifier = Modifier.align(Alignment.CenterEnd),
            onCommentsClick = onCommentsClick,
            onMessageClick = onMessageClick,
            onShareClick = onShareClick
        )

        VsPostQuestionCard(
            question = post.question,
            modifier = Modifier.align(Alignment.Center)
        )

        VsPostVsBadge(
            modifier = Modifier.align(Alignment.Center)
        )

        VsPostResults(
            post = post,
            resultsAlpha = resultsAlpha,
            extraBottomPadding = extraBottomPadding,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}