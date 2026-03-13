package com.nazam.instaclone.feature.home.presentation.ui.explore.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.explore_search_placeholder
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExploreSearchBar(
    query: String,
    suggestions: List<String>,
    onQueryChange: (String) -> Unit,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = ExploreUiTokens.SearchBackground,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "⌕",
                color = ExploreUiTokens.SearchIconColor,
                style = MaterialTheme.typography.titleMedium
            )

            Box(
                modifier = Modifier
                    .background(
                        color = ExploreUiTokens.SearchTagBackground,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "#",
                    color = ExploreUiTokens.SearchTagColor,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                cursorBrush = SolidColor(ExploreUiTokens.SearchCursorColor),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = ExploreUiTokens.SearchTextColor
                ),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (query.isBlank()) {
                        Text(
                            text = stringResource(Res.string.explore_search_placeholder),
                            color = ExploreUiTokens.SearchPlaceholderColor,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    innerTextField()
                }
            )

            if (query.isNotBlank()) {
                Text(
                    text = "✕",
                    color = ExploreUiTokens.SearchClearColor,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.clickable { onQueryChange("") }
                )
            }
        }

        if (query.isBlank() && suggestions.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                suggestions.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .widthIn(min = 52.dp)
                            .background(
                                color = ExploreUiTokens.SuggestionBackground,
                                shape = RoundedCornerShape(999.dp)
                            )
                            .clickable { onSuggestionClick(tag) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "#$tag",
                            color = ExploreUiTokens.SuggestionTextColor,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}
