package com.nazam.instaclone.feature.home.presentation.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.nazam.instaclone.feature.home.presentation.model.HomeUiState
import com.nazam.instaclone.feature.home.presentation.ui.components.comments.CommentsPanel

@Composable
fun HomeCommentsOverlay(
    ui: HomeUiState,
    bottomOffset: Dp,
    panelHeight: Dp,
    onCloseComments: () -> Unit
) {
    if (!ui.isCommentsSheetOpen) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88000000))
            .clickable { onCloseComments() }
    )

    CommentsPanel(
        bottomOffset = bottomOffset,
        height = panelHeight,
        isLoading = ui.isCommentsLoading,
        comments = ui.comments,
        onClose = onCloseComments
    )
}