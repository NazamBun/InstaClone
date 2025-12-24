package com.nazam.instaclone.feature.home.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import androidx.compose.ui.unit.dp

@Composable
actual fun NetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale
) {
    if (url.isBlank()) {
        Box(modifier = modifier.background(Color(0xFF1A1A1A)))
        return
    }

    KamelImage(
        resource = { asyncPainterResource(url) },
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        onLoading = {
            Box(
                modifier = modifier.background(Color(0xFF1A1A1A)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(strokeWidth = 2.dp)
            }
        },
        onFailure = {
            Box(
                modifier = modifier.background(Color(0xFF1A1A1A)),
                contentAlignment = Alignment.Center
            ) {
                Text("Image", color = Color(0xFFBBBBBB))
            }
        }
    )
}