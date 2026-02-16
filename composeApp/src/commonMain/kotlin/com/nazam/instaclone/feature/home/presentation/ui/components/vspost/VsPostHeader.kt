package com.nazam.instaclone.feature.home.presentation.ui.components.vspost

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nazam.instaclone.feature.home.domain.model.VoteCategories

@Composable
fun VsPostHeader(
    authorName: String,
    category: String,
    modifier: Modifier = Modifier
) {
    // category = categoryId (ex: "sport"), on veut afficher "Sport"
    val categoryLabel = VoteCategories.labelFor(category)

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = authorName,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = categoryLabel,
            color = Color.White,
            fontSize = 12.sp
        )
    }
}
