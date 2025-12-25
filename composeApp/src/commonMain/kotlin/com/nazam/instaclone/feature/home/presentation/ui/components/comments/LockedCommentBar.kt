package com.nazam.instaclone.feature.home.presentation.ui.components.comments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LockedCommentBar(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color(0xFF0B0B10)
    ) {
        Text(
            "Connecte-toi pour commenter",
            color = Color(0xFFBBBBBB),
            modifier = Modifier.padding(16.dp)
        )
    }
}