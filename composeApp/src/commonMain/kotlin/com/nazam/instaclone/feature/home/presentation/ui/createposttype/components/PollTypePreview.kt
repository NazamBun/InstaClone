package com.nazam.instaclone.feature.home.presentation.ui.createposttype.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DuelPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PlaceholderBox("A", Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.LightGray.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("VS")
        }
        PlaceholderBox("B", Modifier.weight(1f))
    }
}

@Composable
fun YesNoPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PlaceholderLine(1f)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PlaceholderButton("OUI", Modifier.weight(1f))
            PlaceholderButton("NON", Modifier.weight(1f))
        }
    }
}

@Composable
private fun PlaceholderBox(text: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(82.dp)
            .background(Color.LightGray.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text)
    }
}

@Composable
private fun PlaceholderButton(text: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(42.dp)
            .background(Color.LightGray.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text)
    }
}

@Composable
private fun PlaceholderLine(width: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth(width)
            .height(16.dp)
            .background(Color.LightGray.copy(alpha = 0.35f), RoundedCornerShape(50))
    )
}
