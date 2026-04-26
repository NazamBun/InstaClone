package com.nazam.instaclone.feature.home.presentation.ui.createposttype.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DuelPreview() {
    PhonePreview {
        Row(modifier = Modifier.fillMaxWidth()) {
            ImageSide(label = "A", modifier = Modifier.weight(1f))
            ImageSide(label = "B", modifier = Modifier.weight(1f))
        }

        CircleText(
            text = "VS",
            modifier = Modifier.align(Alignment.Center)
        )

        ResultsPreview(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun YesNoPreview() {
    PhonePreview {
        QuestionPreview(modifier = Modifier.align(Alignment.TopCenter))

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ChoiceButton("OUI")
            ChoiceButton("NON")
        }
    }
}

@Composable
private fun PhonePreview(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .width(138.dp)
            .height(172.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFF7F7F8))
            .border(1.dp, Color(0xFFE4E4E7), RoundedCornerShape(28.dp))
    ) {
        content()
    }
}

@Composable
private fun ImageSide(
    label: String,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .height(172.dp)
            .background(Color(0xFFEDEDF0)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color(0xFF6B6B70),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CircleText(
    text: String,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, Color(0xFFE1E1E5), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFF1C1C1E),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun QuestionPreview(modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SoftLine(width = 86)
        SoftLine(width = 58)
    }
}

@Composable
private fun ResultsPreview(modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        SoftLine(width = 104)
        SoftLine(width = 72)
    }
}

@Composable
private fun ChoiceButton(text: String) {
    Box(
        modifier = Modifier
            .width(112.dp)
            .height(38.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SoftLine(width: Int) {
    Box(
        modifier = Modifier
            .width(width.dp)
            .height(9.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFD8D8DD))
    )
}
