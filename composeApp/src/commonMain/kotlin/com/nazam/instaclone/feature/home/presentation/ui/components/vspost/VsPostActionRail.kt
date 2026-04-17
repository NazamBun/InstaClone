package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VsPostActionRail(
    commentsCount: Int,
    onCommentsClick: () -> Unit,
    onMessageClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        IconButton(onClick = onCommentsClick) {
            Icon(
                imageVector = Icons.Outlined.ChatBubbleOutline,
                contentDescription = "comments",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = commentsCount.toString(),
            color = Color.White
        )

        Spacer(Modifier.height(12.dp))

        IconButton(onClick = onMessageClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Send,
                contentDescription = "send",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        IconButton(onClick = onShareClick) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = "share",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
