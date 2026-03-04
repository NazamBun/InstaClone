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

/**
 * Barre de % (KMP friendly)
 * - reverse = true : la barre part du milieu vers la gauche (barre gauche)
 * - reverse = false : la barre part du milieu vers la droite (barre droite)
 *
 * Flame :
 * - 1 seule fois
 * - uniquement si ratio == 1f (100%)
 * - au bout (gauche si reverse, droite sinon)
 */
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

    // ✅ trigger 1 seule fois quand on atteint 100%
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

        // Fill : ✅ part du milieu (côté interne)
        val fillMod = Modifier
            .fillMaxHeight()
            .fillMaxWidth(animatedRatio)
            .background(fill, RoundedCornerShape(50))

        if (reverse) {
            // barre gauche : elle part du milieu -> gauche
            Box(modifier = fillMod.align(Alignment.CenterEnd))
        } else {
            // barre droite : elle part du milieu -> droite
            Box(modifier = fillMod.align(Alignment.CenterStart))
        }

        // Flame : ✅ au bout EXTERNE, au-dessus
        AnimatedVisibility(
            visible = showFlame,
            enter = fadeIn(tween(160)),
            exit = fadeOut(tween(220))
        ) {
            VsPostFlame(
                modifier = Modifier
                    .zIndex(999f)
                    .align(if (reverse) Alignment.CenterStart else Alignment.CenterEnd)
                    .offset(x = if (reverse) (-6).dp else 6.dp, y = (-22).dp)
            )
        }
    }
}

@Composable
private fun VsPostFlame(modifier: Modifier = Modifier) {
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
        modifier = modifier.scale(pulse).alpha(0.98f)
    )
}
