package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

@Composable
internal fun VsPostPercentBar(
    ratio: Float,
    reverse: Boolean,
    fill: Brush,
    modifier: Modifier = Modifier
) {
    val safe = ratio.coerceIn(0f, 1f)
    val animatedRatio = androidx.compose.animation.core.animateFloatAsState(
        targetValue = safe,
        animationSpec = tween(450),
        label = "vsPercentBarRatio"
    ).value

    // ✅ 1 seule fois quand on atteint 100%
    var alreadyShown by remember { mutableStateOf(false) }
    var showFlame by remember { mutableStateOf(false) }

    LaunchedEffect(safe) {
        val reached100 = safe >= 1f
        if (reached100 && !alreadyShown) {
            alreadyShown = true
            showFlame = true
            delay(1800)
            showFlame = false
        }
        if (!reached100) {
            alreadyShown = false
            showFlame = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
    ) {
        // Track
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0x33FFFFFF), RoundedCornerShape(50))
        )

        // Fill : ✅ part du milieu (barre gauche vers gauche, barre droite vers droite)
        val fillMod = Modifier
            .fillMaxHeight()
            .fillMaxWidth(animatedRatio)
            .background(fill, RoundedCornerShape(50))

        if (reverse) {
            Box(modifier = fillMod.align(Alignment.CenterEnd))
        } else {
            Box(modifier = fillMod.align(Alignment.CenterStart))
        }

        // ✅ IMPORTANT : align/zIndex/offset doivent être sur AnimatedVisibility (enfant direct du Box)
        AnimatedVisibility(
            visible = showFlame,
            enter = fadeIn(tween(160)),
            exit = fadeOut(tween(220)),
            modifier = Modifier
                .zIndex(9999f)
                .align(if (reverse) Alignment.CenterStart else Alignment.CenterEnd)
                .offset(x = if (reverse) (-6).dp else 6.dp, y = (-24).dp)
        ) {
            VsPostFlame()
        }
    }
}

@Composable
private fun VsPostFlame() {
    val infinite = rememberInfiniteTransition(label = "vsFlamePulse")
    val pulse by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(tween(420), RepeatMode.Reverse),
        label = "vsFlameScale"
    )

    Text(
        text = "🔥",
        fontSize = 30.sp,
        modifier = Modifier.scale(pulse).alpha(0.98f)
    )
}
