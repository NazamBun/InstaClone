package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun VsPostItem(
    post: VsPost,
    onVoteLeft: () -> Unit,
    onVoteRight: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {

        // 🔥 LES 2 IMAGES PRENNENT TOUT L'ÉCRAN
        Row(
            modifier = Modifier.fillMaxSize()
        ) {

            // IMAGE GAUCHE + clic = vote gauche
            AsyncImage(
                model = post.leftImageUrl,
                contentDescription = post.leftLabel,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onVoteLeft() }
            )

            // IMAGE DROITE + clic = vote droite
            AsyncImage(
                model = post.rightImageUrl,
                contentDescription = post.rightLabel,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onVoteRight() }
            )
        }

        // LÉGER DÉGRADÉ EN BAS POUR QUE LE TEXTE RESTE LISIBLE
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

        // 🧑 AUTEUR + CATÉGORIE EN HAUT À GAUCHE
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

        // ❓ QUESTION AU CENTRE
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

        // ⚪ CERCLE VS
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

        // 📊 LABELS + VOTES EN BAS
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(20.dp),
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