package com.nazam.instaclone.feature.home.presentation.ui.components.comments

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.Comment
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.comments_empty
import instaclone.composeapp.generated.resources.comments_title
import instaclone.composeapp.generated.resources.dialog_cancel
import org.jetbrains.compose.resources.stringResource

@Composable
fun CommentsPanel(
    bottomOffset: Dp,
    height: Dp,
    isLoading: Boolean,
    comments: List<Comment>,
    onClose: () -> Unit
) {
    val panelShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomOffset),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(panelShape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.62f),
                            Color.Black.copy(alpha = 0.78f),
                            Color.Black.copy(alpha = 0.92f)
                        )
                    )
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.16f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.comments_title),
                        modifier = Modifier.weight(1f),
                        color = Color.White
                    )

                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(Res.string.dialog_cancel),
                            tint = Color.White
                        )
                    }
                }

                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    comments.isEmpty() -> {
                        Text(
                            text = stringResource(Res.string.comments_empty),
                            modifier = Modifier.padding(16.dp),
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(comments) { comment ->
                                CommentRow(comment)
                            }
                        }
                    }
                }
            }
        }
    }
}
