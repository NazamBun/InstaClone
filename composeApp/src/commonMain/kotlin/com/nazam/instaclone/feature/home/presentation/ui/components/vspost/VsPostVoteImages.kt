package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.nazam.instaclone.feature.home.domain.model.VoteChoice
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.ui.components.NetworkImage

@Composable
fun VsPostVoteImages(
    post: VsPost,
    isVoting: Boolean,
    onVoteLeft: () -> Unit,
    onVoteRight: () -> Unit,
    onLeftTapPosition: (Offset) -> Unit,
    onRightTapPosition: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    val canClick = !isVoting
    var leftTopInParent by remember(post.id) { mutableStateOf(Offset.Zero) }
    var rightTopInParent by remember(post.id) { mutableStateOf(Offset.Zero) }

    val leftAlpha by animateFloatAsState(
        targetValue = if (post.userVote == VoteChoice.RIGHT) 0.3f else 1f,
        animationSpec = tween(220),
        label = "leftAlpha"
    )
    val rightAlpha by animateFloatAsState(
        targetValue = if (post.userVote == VoteChoice.LEFT) 0.3f else 1f,
        animationSpec = tween(220),
        label = "rightAlpha"
    )

    val leftScale by animateFloatAsState(
        targetValue = if (post.userVote == VoteChoice.LEFT) 1.03f else 1f,
        animationSpec = tween(220),
        label = "leftScale"
    )
    val rightScale by animateFloatAsState(
        targetValue = if (post.userVote == VoteChoice.RIGHT) 1.03f else 1f,
        animationSpec = tween(220),
        label = "rightScale"
    )

    val borderColor = Color(0xFF2F5BFF)
    val borderWidth = 3.dp
    val leftZ = if (post.userVote == VoteChoice.LEFT) 1f else 0f
    val rightZ = if (post.userVote == VoteChoice.RIGHT) 1f else 0f

    Box(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            NetworkImage(
                url = post.leftImageUrl,
                contentDescription = post.leftLabel,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .zIndex(leftZ)
                    .clipToBounds()
                    .onGloballyPositioned { leftTopInParent = it.positionInParent() }
                    .alpha(leftAlpha)
                    .graphicsLayer(scaleX = leftScale, scaleY = leftScale)
                    .then(
                        if (post.userVote == VoteChoice.LEFT) {
                            Modifier.border(borderWidth, borderColor)
                        } else Modifier
                    )
                    .pointerInput(canClick) {
                        detectTapGestures { tap ->
                            if (!canClick) return@detectTapGestures
                            onLeftTapPosition(leftTopInParent + tap)
                            onVoteLeft()
                        }
                    },
                contentScale = ContentScale.Crop
            )

            NetworkImage(
                url = post.rightImageUrl,
                contentDescription = post.rightLabel,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .zIndex(rightZ)
                    .clipToBounds()
                    .onGloballyPositioned { rightTopInParent = it.positionInParent() }
                    .alpha(rightAlpha)
                    .graphicsLayer(scaleX = rightScale, scaleY = rightScale)
                    .then(
                        if (post.userVote == VoteChoice.RIGHT) {
                            Modifier.border(borderWidth, borderColor)
                        } else Modifier
                    )
                    .pointerInput(canClick) {
                        detectTapGestures { tap ->
                            if (!canClick) return@detectTapGestures
                            onRightTapPosition(rightTopInParent + tap)
                            onVoteRight()
                        }
                    },
                contentScale = ContentScale.Crop
            )
        }

        VsSplitGlowDivider(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun VsSplitGlowDivider(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.width(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(24.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.05f),
                            Color.White.copy(alpha = 0.14f),
                            Color.White.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.18f),
                            Color.White.copy(alpha = 0.65f),
                            Color.White.copy(alpha = 0.18f)
                        )
                    )
                )
        )
    }
}
