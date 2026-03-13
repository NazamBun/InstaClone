package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VsPostHeader(
    authorName: String,
    onAuthorClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = authorName,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = onAuthorClick)
        )
        Text(
            text = "@${authorName.trim()}",
            color = Color.White.copy(alpha = 0.72f),
            fontSize = 12.sp
        )
    }
}
