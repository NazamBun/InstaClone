package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
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

    val leftAlpha = if (post.userVote == VoteChoice.RIGHT) 0.3f else 1f
    val rightAlpha = if (post.userVote == VoteChoice.LEFT) 0.3f else 1f

    val leftBorder =
        if (post.userVote == VoteChoice.LEFT) Modifier.border(3.dp, Color(0xFFFF4EB8)) else Modifier
    val rightBorder =
        if (post.userVote == VoteChoice.RIGHT) Modifier.border(3.dp, Color(0xFFFF4EB8)) else Modifier

    Row(modifier = modifier.fillMaxSize()) {
        NetworkImage(
            url = post.leftImageUrl,
            contentDescription = post.leftLabel,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .alpha(leftAlpha)
                .then(leftBorder)
                .clickable(enabled = canClick) { onVoteLeft() },
            contentScale = ContentScale.Crop
        )

        NetworkImage(
            url = post.rightImageUrl,
            contentDescription = post.rightLabel,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .alpha(rightAlpha)
                .then(rightBorder)
                .clickable(enabled = canClick) { onVoteRight() },
            contentScale = ContentScale.Crop
        )
    }
}