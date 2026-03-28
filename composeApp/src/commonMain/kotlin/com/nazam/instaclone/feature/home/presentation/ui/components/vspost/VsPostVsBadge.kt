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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.vspost_vs_badge
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun VsPostVsBadge(
    badgeId: String,
    modifier: Modifier = Modifier
) {
    val option = VsBadgeCatalog.findById(badgeId)

    Box(
        modifier = modifier.offset(y = 70.dp),
        contentAlignment = Alignment.Center
    ) {
        if (option != null) {
            Image(
                painter = painterResource(option.drawableRes),
                contentDescription = stringResource(option.titleRes),
                modifier = Modifier.size(200.dp)
            )
        } else {
            Box(
                contentAlignment = Alignment.Center
            ) {

                // 🔥 Halo doux (effet fondu)
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // 🔥 Texte VS
                Text(
                    text = stringResource(Res.string.vspost_vs_badge),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 42.sp
                )
            }
        }
    }
}
