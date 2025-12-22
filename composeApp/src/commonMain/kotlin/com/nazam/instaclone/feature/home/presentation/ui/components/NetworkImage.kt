package com.nazam.instaclone.feature.home.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import coil3.compose.AsyncImage
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource


@Composable
fun NetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    BoxWithConstraints(modifier = modifier) {

        // ✅ ANDROID → Coil
        AsyncImage(
            model = url,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale
        )

        // ✅ iOS → Kamel (nouvelle API)
        KamelImage(
            resource = {
                asyncPainterResource(url)
            },
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale
        )
    }
}