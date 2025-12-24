package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nazam.instaclone.feature.home.domain.model.VoteChoice
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.ui.components.NetworkImage
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
    val leftAlpha = if (post.userVote == VoteChoice.RIGHT) 0.3f else 1f
    val rightAlpha = if (post.userVote == VoteChoice.LEFT) 0.3f else 1f

    val leftBorderModifier =
        if (post.userVote == VoteChoice.LEFT) Modifier.border(3.dp, Color(0xFFFF4EB8))
        else Modifier

    val rightBorderModifier =
        if (post.userVote == VoteChoice.RIGHT) Modifier.border(3.dp, Color(0xFFFF4EB8))
        else Modifier

    val total = post.totalVotesCount.coerceAtLeast(1)
    val leftPercent = (post.leftVotesCount * 100f) / total
    val rightPercent = (post.rightVotesCount * 100f) / total

    val leftRatio = (leftPercent / 100f).coerceIn(0f, 1f)
    val rightRatio = (rightPercent / 100f).coerceIn(0f, 1f)

    val canClick = !isVoting

    Box(modifier = modifier.fillMaxSize()) {

        // ================= IMAGES =================
        Row(modifier = Modifier.fillMaxSize()) {

            NetworkImage(
                url = post.leftImageUrl,
                contentDescription = post.leftLabel,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .alpha(leftAlpha)
                    .then(leftBorderModifier)
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
                    .then(rightBorderModifier)
                    .clickable(enabled = canClick) { onVoteRight() },
                contentScale = ContentScale.Crop
            )
        }

        // ================= LOADER =================
        if (isVoting) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(0x66000000)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFFF4EB8))
            }
        }

        // ================= GRADIENT =================
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color(0xAA000000)
                        )
                    )
                )
        )

        // ================= HEADER =================
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text(text = post.authorName, color = Color.White, fontWeight = FontWeight.Bold)
            Text(text = post.category, color = Color.White, fontSize = 12.sp)
        }

        // ================= ACTIONS =================
        ActionRail(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp),
            onCommentsClick = onCommentsClick,
            onMessageClick = onMessageClick,
            onShareClick = onShareClick
        )

        // ================= QUESTION =================
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xAA000000))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = post.question,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }

        // ================= VS =================
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 90.dp)
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFFF4EB8), Color(0xFFFF9F3F))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "VS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        // ================= RESULTS =================
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 12.dp,
                    bottom = 12.dp + extraBottomPadding
                )
                .alpha(resultsAlpha)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {

                // GAUCHE: barre va de droite -> gauche
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = post.leftLabel, color = Color.White)
                    Text(text = "${post.leftVotesCount} votes", color = Color.White, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "${leftPercent.roundToInt()}%", color = Color.White, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0x33FFFFFF))
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight()
                                .fillMaxWidth(leftRatio)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF7B61FF), Color(0xFFB95CFF))
                                    )
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // DROITE: barre normale gauche -> droite
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(text = post.rightLabel, color = Color.White)
                    Text(text = "${post.rightVotesCount} votes", color = Color.White, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "${rightPercent.roundToInt()}%", color = Color.White, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0x33FFFFFF))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(rightRatio)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFFF9F3F), Color(0xFFFF4EB8))
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionRail(
    modifier: Modifier = Modifier,
    onCommentsClick: () -> Unit,
    onMessageClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x66000000))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onCommentsClick) {
            Icon(
                imageVector = Icons.Outlined.ChatBubbleOutline,
                contentDescription = "Commentaires",
                tint = Color.White
            )
        }

        IconButton(onClick = onMessageClick) {
            Icon(
                imageVector = Icons.Outlined.Send,
                contentDescription = "Message",
                tint = Color.White
            )
        }

        IconButton(onClick = onShareClick) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = "Partager",
                tint = Color.White
            )
        }
    }
}