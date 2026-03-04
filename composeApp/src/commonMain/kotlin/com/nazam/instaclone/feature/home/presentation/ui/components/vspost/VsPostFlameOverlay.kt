package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.offset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * 🔥 "fumée" : plusieurs petites flammes qui montent + fondu.
 * - Le parent gère position + zIndex (important).
 * - triggerKey permet de relancer l'anim proprement.
 */
@Composable
internal fun VsPostFlameOverlay(
    visible: Boolean,
    triggerKey: Int,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        // On crée N particules. Chacune a sa propre animation.
        val count = 7
        val y = remember(triggerKey) { List(count) { Animatable(0f) } }
        val x = remember(triggerKey) { List(count) { Animatable(0f) } }
        val a = remember(triggerKey) { List(count) { Animatable(0f) } }
        val s = remember(triggerKey) { List(count) { Animatable(1f) } }

        LaunchedEffect(triggerKey) {
            if (triggerKey == 0) return@LaunchedEffect
            val r = Random(triggerKey)

            // On relance toutes les particules
            repeat(count) { i ->
                launch {
                    // petit délai pour donner un effet "fumée"
                    delay((i * 70L) + r.nextLong(0, 40))

                    val targetY = -r.nextInt(50, 90).toFloat()      // monte
                    val targetX = r.nextInt(-18, 18).toFloat()      // dérive gauche/droite

                    a[i].snapTo(0f)
                    y[i].snapTo(0f)
                    x[i].snapTo(0f)
                    s[i].snapTo(r.nextFloat() * 0.15f + 0.95f)

                    // fade in rapide
                    a[i].animateTo(1f, tween(120))

                    // montée + drift
                    launch { y[i].animateTo(targetY, tween(900)) }
                    launch { x[i].animateTo(targetX, tween(900)) }
                    launch { s[i].animateTo(s[i].value + 0.25f, tween(900)) }

                    // fade out à la fin
                    delay(520)
                    a[i].animateTo(0f, tween(420))
                }
            }
        }

        Box(modifier = modifier) {
            repeat(count) { i ->
                Text(
                    text = "🔥",
                    fontSize = 22.sp,
                    modifier = Modifier
                        .offsetPx(x[i].value, y[i].value)
                        .alpha(a[i].value)
                        .scale(s[i].value)
                )
            }
        }
    }
}

private fun Modifier.offsetPx(x: Float, y: Float): Modifier =
    this.then(
        Modifier.offset {
            androidx.compose.ui.unit.IntOffset(x.roundToInt(), y.roundToInt())
        }
    )
