package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nazam.instaclone.feature.home.domain.model.VsPost
import kotlin.math.roundToInt

@Composable
fun VsPostResults(
    post: VsPost,
    resultsAlpha: Float,
    extraBottomPadding: Dp,
    modifier: Modifier = Modifier
) {
    val total = post.totalVotesCount.coerceAtLeast(1)
    val leftPercent = (post.leftVotesCount * 100f) / total
    val rightPercent = (post.rightVotesCount * 100f) / total
    val leftRatio = (leftPercent / 100f).coerceIn(0f, 1f)
    val rightRatio = (rightPercent / 100f).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp + extraBottomPadding)
            .alpha(resultsAlpha)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {

            Column(modifier = Modifier.weight(1f)) {
                Text(post.leftLabel, color = Color.White)
                Text("${post.leftVotesCount} votes", color = Color.White, fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))
                Text("${leftPercent.roundToInt()}%", color = Color.White, fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
                PercentBar(
                    ratio = leftRatio,
                    reverse = true,
                    fill = Brush.horizontalGradient(listOf(Color(0xFF7B61FF), Color(0xFFB95CFF)))
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(post.rightLabel, color = Color.White)
                Text("${post.rightVotesCount} votes", color = Color.White, fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))
                Text("${rightPercent.roundToInt()}%", color = Color.White, fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
                PercentBar(
                    ratio = rightRatio,
                    reverse = false,
                    fill = Brush.horizontalGradient(listOf(Color(0xFFFF9F3F), Color(0xFFFF4EB8)))
                )
            }
        }
    }
}

@Composable
private fun PercentBar(
    ratio: Float,
    reverse: Boolean,
    fill: Brush
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(Color(0x33FFFFFF), RoundedCornerShape(50))
    ) {
        val inner = Modifier
            .fillMaxHeight()
            .fillMaxWidth(ratio)
            .background(fill, RoundedCornerShape(50))

        if (reverse) {
            Box(modifier = inner.align(Alignment.CenterEnd))
        } else {
            Box(modifier = inner)
        }
    }
}