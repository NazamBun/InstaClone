package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
import kotlin.math.roundToInt

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
    val density = LocalDensity.current

    // On garde l'ancien vote pour détecter un vrai changement.
    var lastVote by remember(post.id) { mutableStateOf(post.userVote) }

    // Dernière position tap (dans le Box parent)
    var lastTapLeft by remember(post.id) { mutableStateOf<Offset?>(null) }
    var lastTapRight by remember(post.id) { mutableStateOf<Offset?>(null) }

    // Position finale du "+1"
    var plusOnePos by remember(post.id) { mutableStateOf<Offset?>(null) }
    var showPlusOne by remember(post.id) { mutableStateOf(false) }

    // Quand le vote est confirmé (userVote change), on montre le +1 au bon endroit
    LaunchedEffect(post.id, post.userVote) {
        val newVote = post.userVote
        val oldVote = lastVote

        if (newVote != oldVote && newVote != VoteChoice.NONE) {
            plusOnePos = when (newVote) {
                VoteChoice.LEFT -> lastTapLeft
                VoteChoice.RIGHT -> lastTapRight
                VoteChoice.NONE -> null
            }

            showPlusOne = true
            delay(750)
            showPlusOne = false
        }

        lastVote = newVote
    }

    Box(modifier = modifier.fillMaxSize()) {

        VsPostVoteImages(
            post = post,
            isVoting = isVoting,
            onVoteLeft = onVoteLeft,
            onVoteRight = onVoteRight,
            onLeftTapPosition = { tap -> lastTapLeft = tap },
            onRightTapPosition = { tap -> lastTapRight = tap }
        )

        // "+1" exactement près du doigt
        plusOnePos?.let { p ->
            val shiftX = with(density) { 10.dp.toPx() } // centre un peu le texte
            val shiftY = with(density) { 24.dp.toPx() } // le fait partir un peu au-dessus du doigt

            VotePlusOneOverlay(
                visible = showPlusOne,
                modifier = Modifier
                    .zIndex(3f)
                    .then(
                        Modifier.offsetPx(
                            x = (p.x - shiftX).roundToInt(),
                            y = (p.y - shiftY).roundToInt()
                        )
                    )
            )
        }

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

/**
 * Petit helper : offset en pixels (pratique pour des coordonnées de tap)
 */
private fun Modifier.offsetPx(x: Int, y: Int): Modifier =
    this.then(
        androidx.compose.ui.Modifier.offset {
            androidx.compose.ui.unit.IntOffset(x, y)
        }
    )
