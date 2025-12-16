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
import com.nazam.instaclone.feature.home.domain.model.VoteChoice
import com.nazam.instaclone.feature.home.domain.model.VsPost

@Composable
fun VsPostItem(
    post: VsPost,
    isVoting: Boolean, // 🔒 verrou global pendant la requête
    onVoteLeft: () -> Unit,
    onVoteRight: () -> Unit,
    resultsAlpha: Float,
    modifier: Modifier = Modifier
) {
    // --- Effets visuels selon le vote ---
    val leftAlpha = if (post.userVote == VoteChoice.RIGHT) 0.3f else 1f
    val rightAlpha = if (post.userVote == VoteChoice.LEFT) 0.3f else 1f

    val leftBorderModifier =
        if (post.userVote == VoteChoice.LEFT) {
            Modifier.border(
                width = 3.dp,
                color = Color(0xFFFF4EB8),
                shape = RoundedCornerShape(0.dp)
            )
        } else Modifier

    val rightBorderModifier =
        if (post.userVote == VoteChoice.RIGHT) {
            Modifier.border(
                width = 3.dp,
                color = Color(0xFFFF4EB8),
                shape = RoundedCornerShape(0.dp)
            )
        } else Modifier

    // --- Pourcentages (barres) ---
    val total = post.totalVotesCount.coerceAtLeast(1) // évite /0
    val leftPercent = (post.leftVotesCount * 100f) / total
    val rightPercent = (post.rightVotesCount * 100f) / total
    val leftRatio = (leftPercent / 100f).coerceIn(0f, 1f)
    val rightRatio = (rightPercent / 100f).coerceIn(0f, 1f)

    // 🔒 Blocage clics :
    // - si vote en cours -> bloqué
    // - si déjà voté à gauche -> on bloque le clic gauche
    // - si déjà voté à droite -> on bloque le clic droite
    val canClickLeft = !isVoting && post.userVote != VoteChoice.LEFT
    val canClickRight = !isVoting && post.userVote != VoteChoice.RIGHT

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
                    .clickable(enabled = canClickLeft) { onVoteLeft() } // ✅ BLOQUÉ
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
                    .clickable(enabled = canClickRight) { onVoteRight() } // ✅ BLOQUÉ
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

        // ✅ Zone résultats en bas : chaque moitié a sa barre
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .alpha(resultsAlpha)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // --------- Côté gauche ---------
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = post.leftLabel, color = Color.White)
                    Text(
                        text = "${post.leftVotesCount} votes",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${leftPercent.toInt()}%",
                        color = Color.White,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Barre de progression photo gauche (remplissage droite -> gauche)
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
                                        listOf(
                                            Color(0xFF7B61FF),
                                            Color(0xFFB95CFF)
                                        )
                                    )
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // --------- Côté droit ---------
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(text = post.rightLabel, color = Color.White)
                    Text(
                        text = "${post.rightVotesCount} votes",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${rightPercent.toInt()}%",
                        color = Color.White,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Barre de progression photo droite (remplissage gauche -> droite)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0x33FFFFFF))
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .fillMaxHeight()
                                .fillMaxWidth(rightRatio)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFFFF9F3F),
                                            Color(0xFFFF4EB8)
                                        )
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}