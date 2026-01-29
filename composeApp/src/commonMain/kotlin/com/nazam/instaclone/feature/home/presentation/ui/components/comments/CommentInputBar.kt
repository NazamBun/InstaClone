package com.nazam.instaclone.feature.home.presentation.ui.components.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.comment_placeholder
import org.jetbrains.compose.resources.stringResource

/**
 * Barre d'input commentaire.
 * ✅ KMP friendly : strings via composeResources
 */
@Composable
fun CommentInputBar(
    letter: String,
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0B0B10))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFFF4EB8), Color(0xFFFF9F3F))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = letter, color = Color.White)
        }

        Spacer(modifier = Modifier.width(8.dp))

        TextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text(stringResource(Res.string.comment_placeholder)) },
            singleLine = true
        )

        IconButton(
            onClick = onSend,
            enabled = text.isNotBlank()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = null
            )
        }
    }
}