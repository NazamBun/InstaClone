package com.nazam.instaclone.feature.home.presentation.ui.components.comments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.Comment
import instaclone.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun CommentsPanel(
    bottomOffset: Dp,
    height: Dp,
    isLoading: Boolean,
    comments: List<Comment>,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomOffset),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            color = Color(0xFF0B0B10),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.comments_title),
                        modifier = Modifier.weight(1f),
                        color = Color.White
                    )

                    IconButton(onClick = onClose) {
                        Text(text = stringResource(Res.string.close), color = Color.White)
                    }
                }

                when {
                    isLoading -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))

                    comments.isEmpty() -> Text(
                        text = stringResource(Res.string.comments_empty),
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray
                    )

                    else -> LazyColumn {
                        items(comments) { CommentRow(it) }
                    }
                }
            }
        }
    }
}