package com.nazam.instaclone.feature.home.presentation.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.FeedMode
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.home_feed_following
import instaclone.composeapp.generated.resources.home_feed_for_you
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeFeedTabs(
    selected: FeedMode,
    onSelected: (FeedMode) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .background(Color(0x66000000), RoundedCornerShape(50))
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        FeedTab(
            label = stringResource(Res.string.home_feed_for_you),
            selected = selected == FeedMode.FOR_YOU,
            onClick = { onSelected(FeedMode.FOR_YOU) }
        )
        FeedTab(
            label = stringResource(Res.string.home_feed_following),
            selected = selected == FeedMode.FOLLOWING,
            onClick = { onSelected(FeedMode.FOLLOWING) }
        )
    }
}

@Composable
private fun FeedTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (selected) Color(0xFF2F5BFF) else Color.Transparent,
        shape = RoundedCornerShape(50),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
