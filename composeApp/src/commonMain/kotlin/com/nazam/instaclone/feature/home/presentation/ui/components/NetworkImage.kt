package com.nazam.instaclone.feature.home.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

/**
 * ✅ KMP PRO
 * commonMain ne dépend pas de Coil (Android) ni de Kamel (iOS).
 * Chaque plateforme fournit son "actual".
 */
@Composable
expect fun NetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
)