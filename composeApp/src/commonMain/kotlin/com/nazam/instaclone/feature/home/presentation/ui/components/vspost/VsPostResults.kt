package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nazam.instaclone.feature.home.domain.model.VsPost
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.percent_value
import instaclone.composeapp.generated.resources.vspost_votes_count
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun VsPostResults(
    post: VsPost,
    resultsAlpha: Float,
    extraBottomPadding: Dp,
    modifier: Modifier = Modifier,
    onLeftReached100EndInWindow: (Offset) -> Unit = {},
    onRightReached100EndInWindow: (Offset) -> Unit = {}
) {
    val total = post.totalVotesCount.coerceAtLeast(1)
    val leftPercent = ((post.leftVotesCount * 100f) / total).coerceIn(0f, 100f)
    val rightPercent = ((post.rightVotesCount * 100f) / total).coerceIn(0f, 100f)

    val leftInt = leftPercent.roundToInt()
    val rightInt = rightPercent.roundToInt()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp + extraBottomPadding)
            .alpha(resultsAlpha)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {

            // ✅ Gauche : part du milieu -> gauche
            Column(modifier = Modifier.weight(1f)) {
                Text(post.leftLabel, color = Color.White)
                Text(
                    text = stringResource(Res.string.vspost_votes_count, post.leftVotesCount),
                    color = Color.White,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.percent_value, leftInt),
                    color = Color.White,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(6.dp))

                VsPostPercentBar(
                    ratio = leftPercent / 100f,
                    reverse = true,
                    fill = Brush.horizontalGradient(listOf(Color(0xFF7B61FF), Color(0xFFB95CFF))),
                    onReached100EndInWindow = onLeftReached100EndInWindow
                )
            }

            Spacer(Modifier.width(12.dp))

            // ✅ Droite : part du milieu -> droite
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(post.rightLabel, color = Color.White)
                Text(
                    text = stringResource(Res.string.vspost_votes_count, post.rightVotesCount),
                    color = Color.White,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.percent_value, rightInt),
                    color = Color.White,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(6.dp))

                VsPostPercentBar(
                    ratio = rightPercent / 100f,
                    reverse = false,
                    fill = Brush.horizontalGradient(listOf(Color(0xFFFF9F3F), Color(0xFF2F5BFF))),
                    onReached100EndInWindow = onRightReached100EndInWindow
                )
            }
        }
    }
}
