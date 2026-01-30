package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import instaclone.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun VsPostActionRail(
    onCommentsClick: () -> Unit,
    onMessageClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x66000000))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onCommentsClick) {
            Icon(
                imageVector = Icons.Outlined.ChatBubbleOutline,
                contentDescription = stringResource(Res.string.cd_comments),
                tint = Color.White
            )
        }
        IconButton(onClick = onMessageClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Send,
                contentDescription = stringResource(Res.string.cd_message),
                tint = Color.White
            )
        }
        IconButton(onClick = onShareClick) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = stringResource(Res.string.cd_share),
                tint = Color.White
            )
        }
    }
}