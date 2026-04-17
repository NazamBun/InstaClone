package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.presentation.ui.components.NetworkImage

@Composable
fun VsPostHeader(
    authorName: String,
    authorAvatarUrl: String?,
    subtitle: String,
    onAuthorClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayName = authorName.ifBlank { "Utilisateur" }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onAuthorClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Avatar
        if (!authorAvatarUrl.isNullOrBlank()) {
            NetworkImage(
                url = authorAvatarUrl,
                contentDescription = displayName,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            )
        }

        Spacer(Modifier.width(10.dp))

        Column {
            Text(
                text = displayName,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.7f)
            )
        }

        Spacer(Modifier.weight(1f))

        // Bouton suivre style TikTok
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Suivre",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
