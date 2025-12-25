package com.nazam.instaclone.feature.home.presentation.ui.components.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.presentation.ui.components.NetworkImage

@Composable
fun CommentAvatar(url: String?, letter: String) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFF4EB8), Color(0xFFFF9F3F))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!url.isNullOrBlank()) {
            NetworkImage(
                url = url,
                contentDescription = null,
                modifier = Modifier.size(34.dp)
            )
        } else {
            androidx.compose.material3.Text(letter, color = Color.White)
        }
    }
}