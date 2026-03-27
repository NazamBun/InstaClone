package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.vs_1
import instaclone.composeapp.generated.resources.vspost_vs_badge
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun VsPostVsBadge(
    badgeId: String,
    modifier: Modifier = Modifier
) {
    val size = 200.dp

    Box(
        modifier = modifier
            .offset(y = 70.dp)
            ,
        contentAlignment = Alignment.Center
    ) {

        if (badgeId == "vs_1") {
            Image(
                painter = painterResource(Res.drawable.vs_1),
                contentDescription = stringResource(Res.string.vspost_vs_badge),
                modifier = Modifier.size(size)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF2F5BFF),
                                Color(0xFFFF9F3F)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.vspost_vs_badge),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
            }
        }
    }
}
