package com.nazam.instaclone.feature.profile.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.ui.components.NetworkImage
import com.nazam.instaclone.feature.profile.presentation.ui.ProfileUiTokens
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.profile_posts_empty
import org.jetbrains.compose.resources.stringResource
import kotlin.math.max

/**
 * Grille simple (sans LazyVerticalGrid)
 * - KMP friendly
 * - gère le cas "liste vide"
 */
@Composable
fun ProfileGrid(
    posts: List<VsPost>,
    onPostClick: (VsPost) -> Unit
) {
    val t = ProfileUiTokens

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

        if (posts.isEmpty()) {
            Text(
                text = stringResource(Res.string.profile_posts_empty),
                color = t.TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp)
            )

            // petits placeholders (propre visuellement)
            repeat(2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(t.CardBg)
                                .border(1.dp, t.Border, RoundedCornerShape(16.dp))
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(16.dp))
            return
        }

        val rows = posts.chunked(3)

        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { post ->
                    Box(modifier = Modifier.weight(1f)) {
                        ProfileGridItem(post = post, onClick = { onPostClick(post) })
                    }
                }

                // si la ligne n’a pas 3 items -> on complète avec des espaces
                repeat(3 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }

            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ProfileGridItem(
    post: VsPost,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val t = ProfileUiTokens
    val total = max(post.totalVotesCount, 1)

    // image “winner” simple
    val image =
        if (post.leftVotesCount >= post.rightVotesCount) post.leftImageUrl else post.rightImageUrl

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(t.CardBg)
            .border(1.dp, t.Border, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        if (image.isNotBlank()) {
            NetworkImage(url = image, contentDescription = null, modifier = Modifier.fillMaxSize())
        }

        // petit badge votes (pro + utile)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(t.SoftOverlay)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = total.toString(),
                color = t.TextPrimary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
