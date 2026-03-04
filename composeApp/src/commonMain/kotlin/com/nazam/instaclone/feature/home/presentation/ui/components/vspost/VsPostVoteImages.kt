package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
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

    // Position du composant dans son parent (Box de VsPostItem)
    var leftTopInParent by remember(post.id) { mutableStateOf(Offset.Zero) }
    var rightTopInParent by remember(post.id) { mutableStateOf(Offset.Zero) }

    val leftTargetAlpha = if (post.userVote == VoteChoice.RIGHT) 0.3f else 1f
    val rightTargetAlpha = if (post.userVote == VoteChoice.LEFT) 0.3f else 1f

    val leftAlpha = animateFloatAsState(leftTargetAlpha, tween(220), label = "leftAlpha").value
    val rightAlpha = animateFloatAsState(rightTargetAlpha, tween(220), label = "rightAlpha").value

    val leftTargetScale = if (post.userVote == VoteChoice.LEFT) 1.03f else 1f
    val rightTargetScale = if (post.userVote == VoteChoice.RIGHT) 1.03f else 1f

    val leftScale = animateFloatAsState(leftTargetScale, tween(220), label = "leftScale").value
    val rightScale = animateFloatAsState(rightTargetScale, tween(220), label = "rightScale").value

    val borderColor = Color(0xFF2F5BFF)
    val borderWidth = 3.dp

    val isLeftSelected = post.userVote == VoteChoice.LEFT
    val isRightSelected = post.userVote == VoteChoice.RIGHT

    // IMPORTANT :
    // Row => le 2e enfant peut "cacher" la bordure du 1er au milieu.
    // Donc zIndex : l'image sélectionnée passe au-dessus.
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
                .onGloballyPositioned { coords -> leftTopInParent = coords.positionInParent() }
                .alpha(leftAlpha)
                .graphicsLayer(scaleX = leftScale, scaleY = leftScale)
                .then(if (isLeftSelected) Modifier.border(borderWidth, borderColor) else Modifier)
                .pointerInput(canClick) {
                    detectTapGestures { tap ->
                        if (!canClick) return@detectTapGestures
                        // tap est local à l'image -> on le transforme en coordonnée du parent
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
                .onGloballyPositioned { coords -> rightTopInParent = coords.positionInParent() }
                .alpha(rightAlpha)
                .graphicsLayer(scaleX = rightScale, scaleY = rightScale)
                .then(if (isRightSelected) Modifier.border(borderWidth, borderColor) else Modifier)
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
}
