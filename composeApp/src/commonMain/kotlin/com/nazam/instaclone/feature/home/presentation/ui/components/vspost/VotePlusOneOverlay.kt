package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Petit "+1" animé.
 * - visible = true -> il apparaît
 * - visible = false -> il disparaît
 */
@Composable
fun VotePlusOneOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(120)) + scaleIn(initialScale = 0.85f, animationSpec = tween(180)),
        exit = fadeOut(animationSpec = tween(180)) + scaleOut(targetScale = 0.9f, animationSpec = tween(180))
    ) {
        Box(
            modifier = modifier
                .background(Color(0xFF2F5BFF), RoundedCornerShape(999.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .alpha(0.95f)
        ) {
            Text(
                text = "+1",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
