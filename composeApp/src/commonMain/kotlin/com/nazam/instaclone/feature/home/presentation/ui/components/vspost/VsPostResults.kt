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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.nazam.instaclone.feature.home.domain.model.VsPost
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.percent_value
import instaclone.composeapp.generated.resources.vspost_votes_count
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun VsPostResults(
    post: VsPost,
    resultsAlpha: Float,
    extraBottomPadding: Dp,
    modifier: Modifier = Modifier
) {
    val total = post.totalVotesCount.coerceAtLeast(1)
    val leftPercent = ((post.leftVotesCount * 100f) / total).coerceIn(0f, 100f)
    val rightPercent = ((post.rightVotesCount * 100f) / total).coerceIn(0f, 100f)

    val leftPercentInt = leftPercent.roundToInt()
    val rightPercentInt = rightPercent.roundToInt()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp + extraBottomPadding)
            .alpha(resultsAlpha)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {

            Column(modifier = Modifier.weight(1f)) {
                Text(post.leftLabel, color = Color.White)
                Text(
                    text = stringResource(Res.string.vspost_votes_count, post.leftVotesCount),
                    color = Color.White,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.percent_value, leftPercentInt),
                    color = Color.White,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(6.dp))

                PercentBarWithFlame(
                    ratio = leftPercent / 100f,
                    showFlameTrigger = leftPercentInt == 100,
                    flameOnStart = true, // ✅ au bout gauche
                    fill = Brush.horizontalGradient(listOf(Color(0xFF7B61FF), Color(0xFFB95CFF)))
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(post.rightLabel, color = Color.White)
                Text(
                    text = stringResource(Res.string.vspost_votes_count, post.rightVotesCount),
                    color = Color.White,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.percent_value, rightPercentInt),
                    color = Color.White,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(6.dp))

                PercentBarWithFlame(
                    ratio = rightPercent / 100f,
                    showFlameTrigger = rightPercentInt == 100,
                    flameOnStart = false, // ✅ au bout droit
                    fill = Brush.horizontalGradient(listOf(Color(0xFFFF9F3F), Color(0xFF2F5BFF)))
                )
            }
        }
    }
}

@Composable
private fun PercentBarWithFlame(
    ratio: Float,
    showFlameTrigger: Boolean,
    flameOnStart: Boolean,
    fill: Brush
) {
    val safeRatio = ratio.coerceIn(0f, 1f)
    val animatedRatio = androidx.compose.animation.core.animateFloatAsState(
        targetValue = safeRatio,
        animationSpec = tween(450),
        label = "barRatio"
    ).value

    var showFlame by remember { mutableStateOf(false) }

    // ✅ La flamme apparaît 1 fois, puis disparaît
    LaunchedEffect(showFlameTrigger) {
        if (showFlameTrigger) {
            showFlame = true
            delay(2200)
            showFlame = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
    ) {
        // Track
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0x33FFFFFF), RoundedCornerShape(50))
        )

        // Fill
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedRatio)
                .align(Alignment.CenterStart)
                .background(fill, RoundedCornerShape(50))
        )

        // ✅ Flame au-dessus (jamais au milieu)
        AnimatedVisibility(
            visible = showFlame,
            enter = fadeIn(tween(180)),
            exit = fadeOut(tween(260))
        ) {
            Flame(
                modifier = Modifier
                    .zIndex(999f)
                    .align(if (flameOnStart) Alignment.CenterStart else Alignment.CenterEnd)
                    .offset(x = if (flameOnStart) (-6).dp else 6.dp, y = (-22).dp)
            )
        }
    }
}

@Composable
private fun Flame(modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "flamePulse")
    val pulse by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(tween(420), RepeatMode.Reverse),
        label = "flameScale"
    )

    Text(
        text = "🔥",
        fontSize = 22.sp,
        modifier = modifier.scale(pulse).alpha(0.98f)
    )
}
