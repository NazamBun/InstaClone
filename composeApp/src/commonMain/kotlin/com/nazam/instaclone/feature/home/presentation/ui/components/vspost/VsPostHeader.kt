package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nazam.instaclone.feature.home.presentation.ui.components.NetworkImage

@Composable
fun VsPostHeader(
    authorName: String,
    authorAvatarUrl: String?,
    onAuthorClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayName = authorName.ifBlank { "Utilisateur" }
    val handle = displayName.trim().replace(" ", "").lowercase()
    val firstLetter = displayName.firstOrNull()?.uppercase() ?: "?"

    Row(
        modifier = modifier
            .clickable(onClick = onAuthorClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!authorAvatarUrl.isNullOrBlank()) {
            NetworkImage(
                url = authorAvatarUrl,
                contentDescription = displayName,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = firstLetter,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(
                text = displayName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "@$handle",
                color = Color.White.copy(alpha = 0.72f),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
