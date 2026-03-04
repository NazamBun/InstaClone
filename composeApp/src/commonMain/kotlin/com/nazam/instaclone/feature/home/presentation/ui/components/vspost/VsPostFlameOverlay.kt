package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.sp

/**
 * Flamme affichée AU-DESSUS de tout (c'est le parent qui gère zIndex/position).
 */
@Composable
internal fun VsPostFlameOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(140)),
        exit = fadeOut(tween(260))
    ) {
        val infinite = rememberInfiniteTransition(label = "flamePulse")
        val pulse = infinite.animateFloat(
            initialValue = 1f,
            targetValue = 1.25f,
            animationSpec = infiniteRepeatable(tween(420), RepeatMode.Reverse),
            label = "flameScale"
        ).value

        Text(
            text = "🔥",
            fontSize = 30.sp,
            modifier = modifier.scale(pulse).alpha(0.98f)
        )
    }
}
