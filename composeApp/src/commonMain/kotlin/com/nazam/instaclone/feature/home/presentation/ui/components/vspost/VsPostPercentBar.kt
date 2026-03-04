package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp

/**
 * Barre % (KMP friendly).
 * - reverse=true : la barre part du milieu -> gauche
 * - reverse=false: la barre part du milieu -> droite
 *
 * IMPORTANT :
 * - Ici on NE dessine PAS la flamme.
 * - On envoie juste la position de fin (en window) quand on atteint 100%.
 */
@Composable
internal fun VsPostPercentBar(
    ratio: Float,
    reverse: Boolean,
    fill: Brush,
    onReached100EndInWindow: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    val safe = ratio.coerceIn(0f, 1f)

    val animated by animateFloatAsState(
        targetValue = safe,
        animationSpec = tween(450),
        label = "vsPercentBarRatio"
    )

    var endInWindow by remember { mutableStateOf<Offset?>(null) }
    var alreadySent by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .onGloballyPositioned { coords ->
                val x = if (reverse) 0f else coords.size.width.toFloat()
                val y = coords.size.height / 2f
                endInWindow = coords.localToWindow(Offset(x, y))
            }
    ) {
        // Track
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0x33FFFFFF), RoundedCornerShape(50))
        )

        // Fill (part du milieu)
        val fillMod = Modifier
            .fillMaxHeight()
            .fillMaxWidth(animated)
            .background(fill, RoundedCornerShape(50))

        if (reverse) {
            Box(modifier = fillMod.align(Alignment.CenterEnd))
        } else {
            Box(modifier = fillMod.align(Alignment.CenterStart))
        }
    }

    // ✅ On envoie 1 seule fois la position quand on atteint 100%
    LaunchedEffect(safe, endInWindow) {
        val reached = safe >= 1f
        if (reached && !alreadySent) {
            endInWindow?.let { onReached100EndInWindow(it) }
            alreadySent = true
        }
        if (!reached) alreadySent = false
    }
}
