package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.ColorFilter
import com.nazam.instaclone.feature.home.domain.model.VoteChoice
import com.nazam.instaclone.feature.home.domain.model.VsPost
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

    val leftBorder =
        if (post.userVote == VoteChoice.LEFT)
            Modifier.border(3.dp, Color(0xFFFF4EB8))
        else Modifier

    val rightBorder =
        if (post.userVote == VoteChoice.RIGHT)
            Modifier.border(3.dp, Color(0xFFFF4EB8))
        else Modifier

    val totalVotes = post.totalVotesCount.coerceAtLeast(1)
    val leftPercent = (post.leftVotesCount * 100f) / totalVotes
    val rightPercent = (post.rightVotesCount * 100f) / totalVotes

    val canClick = !isVoting

    Box(modifier = modifier.fillMaxSize()) {

        // ================= IMAGES =================
        Row(modifier = Modifier.fillMaxSize()) {

            NetworkImage(
                url = post.leftImageUrl,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .alpha(leftAlpha)
                    .then(leftBorder)
                    .clickable(enabled = canClick) { onVoteLeft() }
            )

            NetworkImage(
                url = post.rightImageUrl,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .alpha(rightAlpha)
                    .then(rightBorder)
                    .clickable(enabled = canClick) { onVoteRight() }
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
                        listOf(
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
            Text(post.authorName, color = Color.White, fontWeight = FontWeight.Bold)
            Text(post.category, color = Color.White, fontSize = 12.sp)
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
            Text("VS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        // ================= RESULTS =================
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 12.dp + extraBottomPadding
                )
                .alpha(resultsAlpha)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {

                ResultBar(
                    label = post.leftLabel,
                    votes = post.leftVotesCount,
                    percent = leftPercent,
                    alignEnd = true,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(12.dp))

                ResultBar(
                    label = post.rightLabel,
                    votes = post.rightVotesCount,
                    percent = rightPercent,
                    alignEnd = false,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NetworkImage(
    url: String,
    modifier: Modifier = Modifier
) {
    // ✅ évite crash/erreurs si l’url est vide
    if (url.isBlank()) {
        Box(modifier = modifier.background(Color.DarkGray))
        return
    }

    // ✅ KMP (Android + iOS) : Kamel
    // Nouvelle API (plus de dépréciation)
    KamelImage(
        resource = { asyncPainterResource(data = url) },
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        onLoading = {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(0xFF1A1A1A)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFFF4EB8), strokeWidth = 2.dp)
            }
        },
        onFailure = {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(0xFF1A1A1A)),
                contentAlignment = Alignment.Center
            ) {
                Text("Image", color = Color(0xFFBBBBBB), fontSize = 12.sp)
            }
        }
    )
}

@Composable
private fun ResultBar(
    label: String,
    votes: Int,
    percent: Float,
    alignEnd: Boolean,
    modifier: Modifier = Modifier
) {
    val ratio = (percent / 100f).coerceIn(0f, 1f)

    Column(
        modifier = modifier,
        horizontalAlignment = if (alignEnd) Alignment.Start else Alignment.End
    ) {
        Text(text = label, color = Color.White)
        Text(text = "$votes votes", color = Color.White, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "${percent.roundToInt()}%", color = Color.White, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0x33FFFFFF))
        ) {
            // ✅ gauche = progression droite -> gauche (alignEnd = true)
            // ✅ droite = progression gauche -> droite (alignEnd = false)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(ratio)
                    .align(if (alignEnd) Alignment.CenterEnd else Alignment.CenterStart)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            if (alignEnd) {
                                listOf(Color(0xFF7B61FF), Color(0xFFB95CFF))
                            } else {
                                listOf(Color(0xFFFF9F3F), Color(0xFFFF4EB8))
                            }
                        )
                    )
            )
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