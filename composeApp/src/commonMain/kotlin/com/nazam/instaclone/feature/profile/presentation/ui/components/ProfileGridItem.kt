package com.nazam.instaclone.feature.profile.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.presentation.ui.components.NetworkImage

/**
 * Une cellule de post
 */
@Composable
fun ProfileGridItem(
    post: VsPost,
    onClick: () -> Unit
) {

    val image =
        if (post.leftVotesCount >= post.rightVotesCount)
            post.leftImageUrl
        else
            post.rightImageUrl

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {

        NetworkImage(
            url = image,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

    }

}
