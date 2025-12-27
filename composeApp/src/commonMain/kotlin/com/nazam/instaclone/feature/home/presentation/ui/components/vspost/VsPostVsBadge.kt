package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VsPostVsBadge(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .offset(y = 90.dp)
            .size(64.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(Color(0xFFFF4EB8), Color(0xFFFF9F3F)))),
        contentAlignment = Alignment.Center
    ) {
        Text("VS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
    }
}