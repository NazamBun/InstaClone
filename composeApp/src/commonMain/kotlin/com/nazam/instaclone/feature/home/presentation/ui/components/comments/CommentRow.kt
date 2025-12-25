package com.nazam.instaclone.feature.home.presentation.ui.components.comments

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nazam.instaclone.feature.home.domain.model.Comment

@Composable
fun CommentRow(comment: Comment) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        CommentAvatar(
            url = comment.authorAvatarUrl,
            letter = comment.authorName?.firstOrNull()?.uppercase() ?: "?"
        )

        Spacer(Modifier.width(8.dp))

        Column(Modifier.weight(1f)) {
            Text(
                comment.authorName ?: "Utilisateur",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(comment.content, color = Color.White, fontSize = 14.sp)
        }
    }
}