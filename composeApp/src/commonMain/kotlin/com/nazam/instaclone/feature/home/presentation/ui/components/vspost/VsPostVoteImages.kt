package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
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
    modifier: Modifier = Modifier
) {
    val canClick = !isVoting

    val leftTargetAlpha = if (post.userVote == VoteChoice.RIGHT) 0.3f else 1f
    val rightTargetAlpha = if (post.userVote == VoteChoice.LEFT) 0.3f else 1f

    val leftAlpha = animateFloatAsState(
        targetValue = leftTargetAlpha,
        animationSpec = tween(220),
        label = "leftAlpha"
    ).value

    val rightAlpha = animateFloatAsState(
        targetValue = rightTargetAlpha,
        animationSpec = tween(220),
        label = "rightAlpha"
    ).value

    val leftTargetScale = if (post.userVote == VoteChoice.LEFT) 1.03f else 1f
    val rightTargetScale = if (post.userVote == VoteChoice.RIGHT) 1.03f else 1f

    val leftScale = animateFloatAsState(
        targetValue = leftTargetScale,
        animationSpec = tween(220),
        label = "leftScale"
    ).value

    val rightScale = animateFloatAsState(
        targetValue = rightTargetScale,
        animationSpec = tween(220),
        label = "rightScale"
    ).value

    val borderColor = Color(0xFFFF4EB8)
    val borderWidth = 3.dp

    val isLeftSelected = post.userVote == VoteChoice.LEFT
    val isRightSelected = post.userVote == VoteChoice.RIGHT

    // IMPORTANT :
    // Dans un Row, le 2e enfant est dessiné après le 1er => il peut cacher la bordure du 1er au milieu.
    // Donc on met un zIndex : l'image sélectionnée passe au-dessus.
    val leftZ = if (isLeftSelected) 1f else 0f
    val rightZ = if (isRightSelected) 1f else 0f

    Row(modifier = modifier.fillMaxSize()) {
        NetworkImage(
            url = post.leftImageUrl,
            contentDescription = post.leftLabel,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .zIndex(leftZ)
                .clipToBounds()
                .alpha(leftAlpha)
                .graphicsLayer(scaleX = leftScale, scaleY = leftScale)
                .then(if (isLeftSelected) Modifier.border(borderWidth, borderColor) else Modifier)
                .clickable(enabled = canClick) { onVoteLeft() },
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
                .alpha(rightAlpha)
                .graphicsLayer(scaleX = rightScale, scaleY = rightScale)
                .then(if (isRightSelected) Modifier.border(borderWidth, borderColor) else Modifier)
                .clickable(enabled = canClick) { onVoteRight() },
            contentScale = ContentScale.Crop
        )
    }
}
