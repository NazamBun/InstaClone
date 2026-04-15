package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.nazam.instaclone.feature.home.domain.model.VoteChoice
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.ui.components.comments.InlineCommentBar
import com.nazam.instaclone.feature.home.presentation.ui.components.utils.formatTimeAgo
import com.nazam.instaclone.feature.home.presentation.ui.components.vspost.VotePlusOneOverlay
import com.nazam.instaclone.feature.home.presentation.ui.components.vspost.VsPostActionRail
import com.nazam.instaclone.feature.home.presentation.ui.components.vspost.VsPostFlameOverlay
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
    onAuthorClick: (VsPost) -> Unit,
    resultsAlpha: Float,
    modifier: Modifier = Modifier,
    onCommentsClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    extraBottomPadding: Dp = 0.dp
) {
    val density = LocalDensity.current

    var rootInWindow by remember(post.id) { mutableStateOf(Offset.Zero) }
    var lastVote by remember(post.id) { mutableStateOf(post.userVote) }
    var showQuestion by remember(post.id) { mutableStateOf(true) }
    var lastTapLeft by remember(post.id) { mutableStateOf<Offset?>(null) }
    var lastTapRight by remember(post.id) { mutableStateOf<Offset?>(null) }
    var plusOnePos by remember(post.id) { mutableStateOf<Offset?>(null) }
    var showPlusOne by remember(post.id) { mutableStateOf(false) }
    var flamePosPx by remember(post.id) { mutableStateOf<IntOffset?>(null) }
    var showFlame by remember(post.id) { mutableStateOf(false) }
    var flameTriggerKey by remember(post.id) { mutableStateOf(0) }

    LaunchedEffect(post.id) {
        delay(1200)
        showQuestion = false
    }

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

    fun triggerFlame(endInWindow: Offset) {
        val local = endInWindow - rootInWindow
        val dx = with(density) { 6.dp.toPx() }
        val dy = with(density) { 26.dp.toPx() }

        flamePosPx = IntOffset(
            x = (local.x - dx).roundToInt(),
            y = (local.y - dy).roundToInt()
        )
        flameTriggerKey += 1
    }

    LaunchedEffect(flameTriggerKey) {
        if (flameTriggerKey != 0) {
            showFlame = true
            delay(1800)
            showFlame = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { rootInWindow = it.positionInWindow() }
    ) {
        VsPostVoteImages(
            post = post,
            isVoting = isVoting,
            onVoteLeft = onVoteLeft,
            onVoteRight = onVoteRight,
            onLeftTapPosition = { tap -> lastTapLeft = tap },
            onRightTapPosition = { tap -> lastTapRight = tap }
        )

        plusOnePos?.let { p ->
            val shiftX = with(density) { 10.dp.toPx() }
            val shiftY = with(density) { 24.dp.toPx() }

            VotePlusOneOverlay(
                visible = showPlusOne,
                modifier = Modifier
                    .zIndex(20f)
                    .offsetPx(
                        x = (p.x - shiftX).roundToInt(),
                        y = (p.y - shiftY).roundToInt()
                    )
            )
        }

        if (isVoting) {
            VsPostVotingOverlay()
        }

        VsPostOverlayGradient()

        VsPostHeader(
            authorName = post.authorName,
            authorAvatarUrl = post.authorAvatarUrl,
            subtitle = formatTimeAgo(post.createdAt),
            onAuthorClick = { onAuthorClick(post) },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 44.dp)
        )

        VsPostActionRail(
            commentsCount = post.commentsCount,
            modifier = Modifier.align(Alignment.CenterEnd),
            onCommentsClick = onCommentsClick,
            onMessageClick = onMessageClick,
            onShareClick = onShareClick
        )

        val alpha = animateFloatAsState(
            targetValue = if (showQuestion) 1f else 0f,
            animationSpec = tween(600)
        )

        VsPostQuestionCard(
            question = post.question,
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(alpha.value)
        )

        VsPostVsBadge(
            badgeId = post.selectedVsBadgeId,
            modifier = Modifier.align(Alignment.Center)
        )

        InlineCommentBar(
            onClick = onCommentsClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 20.dp, end = 20.dp, bottom = 96.dp + extraBottomPadding)
        )

        VsPostResults(
            post = post,
            resultsAlpha = resultsAlpha,
            extraBottomPadding = extraBottomPadding,
            modifier = Modifier.align(Alignment.BottomCenter),
            onLeftReached100EndInWindow = { triggerFlame(it) },
            onRightReached100EndInWindow = { triggerFlame(it) }
        )

        flamePosPx?.let { pos ->
            VsPostFlameOverlay(
                visible = showFlame,
                triggerKey = flameTriggerKey,
                modifier = Modifier
                    .zIndex(10_000f)
                    .offset { pos }
            )
        }
    }
}

private fun Modifier.offsetPx(x: Int, y: Int): Modifier {
    return this.then(Modifier.offset { IntOffset(x, y) })
}
