package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VotePlusOneOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    val alpha = remember { Animatable(0f) }
    val y = remember { Animatable(0f) }

    LaunchedEffect(visible) {
        if (visible) {
            alpha.snapTo(0f)
            y.snapTo(0f)

            alpha.animateTo(1f, tween(120))
            y.animateTo(-24f, tween(520))
            alpha.animateTo(0f, tween(220))
        } else {
            alpha.snapTo(0f)
            y.snapTo(0f)
        }
    }

    if (alpha.value <= 0f) return

    // ✅ Dégradé bleu moderne (cohérent avec ton UI)
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF4F7CFF), // bleu clair
            Color(0xFF2F5BFF)  // bleu accent
        )
    )

    Text(
        text = "+1",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        style = TextStyle(brush = gradient),
        modifier = modifier
            .offset(y = y.value.dp)
            .alpha(alpha.value)
    )
}
