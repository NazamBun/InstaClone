package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.VoteChoice
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.ui.components.vspost.VotePlusOneOverlay
import com.nazam.instaclone.feature.home.presentation.ui.components.vspost.VsPostActionRail
import com.nazam.instaclone.feature.home.presentation.ui.components.vspost.VsPostHeader
import com.nazam.instaclone.feature.home.presentation.ui.components.vspost.VsPostOverlayGradient
import com.nazam.instaclone.feature.home.presentation.ui.components.vspost.VsPostQuestionCard
import com.nazam.instaclone.feature.home.presentation.ui.components.vspost.VsPostResults
import com.nazam.instaclone.feature.home.presentation.ui.components.vspost.VsPostVoteImages
import com.nazam.instaclone.feature.home.presentation.ui.components.vspost.VsPostVotingOverlay
import com.nazam.instaclone.feature.home.presentation.ui.components.vspost.VsPostVsBadge
import kotlinx.coroutines.delay

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
    // On garde l'ancien vote pour détecter un vrai changement.
    var lastVote by remember(post.id) { mutableStateOf(post.userVote) }

    // Affichage "+1"
    var showPlusOneLeft by remember(post.id) { mutableStateOf(false) }
    var showPlusOneRight by remember(post.id) { mutableStateOf(false) }

    // Déclenche l'animation quand userVote change réellement (vote validé).
    LaunchedEffect(post.id, post.userVote) {
        val newVote = post.userVote
        val oldVote = lastVote

        if (newVote != oldVote && newVote != VoteChoice.NONE) {
            when (newVote) {
                VoteChoice.LEFT -> {
                    showPlusOneLeft = true
                    delay(650)
                    showPlusOneLeft = false
                }
                VoteChoice.RIGHT -> {
                    showPlusOneRight = true
                    delay(650)
                    showPlusOneRight = false
                }
                VoteChoice.NONE -> Unit
            }
        }

        lastVote = newVote
    }

    Box(modifier = modifier.fillMaxSize()) {

        VsPostVoteImages(
            post = post,
            isVoting = isVoting,
            onVoteLeft = onVoteLeft,
            onVoteRight = onVoteRight
        )

        // "+1" au-dessus de l'image choisie
        VotePlusOneOverlay(
            visible = showPlusOneLeft,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .offset(y = (-90).dp)
        )

        VotePlusOneOverlay(
            visible = showPlusOneRight,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .offset(y = (-90).dp)
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
