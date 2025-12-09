package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.domain.model.VoteChoice

@Composable
fun VsPostItem(
    post: VsPost,
    onVoteLeft: () -> Unit,
    onVoteRight: () -> Unit,
    resultsAlpha: Float,
    modifier: Modifier = Modifier
) {
    // --- Effets visuels selon le vote ---
    val leftAlpha =
        if (post.userVote == VoteChoice.RIGHT) 0.3f else 1f

    val rightAlpha =
        if (post.userVote == VoteChoice.LEFT) 0.3f else 1f

    val leftBorderModifier =
        if (post.userVote == VoteChoice.LEFT) {
            Modifier.border(
                width = 3.dp,
                color = Color(0xFFFF4EB8),
                shape = RoundedCornerShape(0.dp)
            )
        } else {
            Modifier
        }

    val rightBorderModifier =
        if (post.userVote == VoteChoice.RIGHT) {
            Modifier.border(
                width = 3.dp,
                color = Color(0xFFFF4EB8),
                shape = RoundedCornerShape(0.dp)
            )
        } else {
            Modifier
        }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // 🔥 Les 2 images prennent tout l'écran
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Image gauche
            AsyncImage(
                model = post.leftImageUrl,
                contentDescription = post.leftLabel,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .alpha(leftAlpha)
                    .then(leftBorderModifier)
                    .clickable { onVoteLeft() }
            )

            // Image droite
            AsyncImage(
                model = post.rightImageUrl,
                contentDescription = post.rightLabel,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .alpha(rightAlpha)
                    .then(rightBorderModifier)
                    .clickable { onVoteRight() }
            )
        }

        // Léger dégradé global pour le texte
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

        // Auteur + catégorie en haut à gauche
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text(
                text = post.authorName,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = post.category,
                color = Color.White,
                fontSize = 12.sp
            )
        }

        // Question au centre
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

        // Cercle "VS"
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 90.dp)
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFFFF4EB8),
                            Color(0xFFFF9F3F)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "VS",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        // Labels + votes en bas (avec fondu sur swipe)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(20.dp)
                .alpha(resultsAlpha),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = post.leftLabel, color = Color.White)
                Text(
                    text = "${post.leftVotesCount} votes",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(text = post.rightLabel, color = Color.White)
                Text(
                    text = "${post.rightVotesCount} votes",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}