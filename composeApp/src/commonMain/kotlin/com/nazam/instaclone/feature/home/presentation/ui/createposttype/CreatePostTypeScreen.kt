package com.nazam.instaclone.feature.home.presentation.ui.createposttype

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.presentation.model.CreatePostType
import com.nazam.instaclone.feature.home.presentation.ui.createposttype.components.DuelPreview
import com.nazam.instaclone.feature.home.presentation.ui.createposttype.components.PollTypeChoiceCard
import com.nazam.instaclone.feature.home.presentation.ui.createposttype.components.YesNoPreview
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.create_post_type_continue
import instaclone.composeapp.generated.resources.create_post_type_duel_desc
import instaclone.composeapp.generated.resources.create_post_type_duel_title
import instaclone.composeapp.generated.resources.create_post_type_subtitle
import instaclone.composeapp.generated.resources.create_post_type_title
import instaclone.composeapp.generated.resources.create_post_type_yes_no_desc
import instaclone.composeapp.generated.resources.create_post_type_yes_no_title
import instaclone.composeapp.generated.resources.explore_back
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostTypeScreen(
    onBackClick: () -> Unit,
    onContinueClick: (CreatePostType) -> Unit,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf(CreatePostType.DUEL) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(Res.string.create_post_type_title)) },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text(stringResource(Res.string.explore_back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = stringResource(Res.string.create_post_type_subtitle),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            PollTypeChoiceCard(
                title = stringResource(Res.string.create_post_type_duel_title),
                description = stringResource(Res.string.create_post_type_duel_desc),
                tag = "A vs B",
                selected = selected == CreatePostType.DUEL,
                onClick = { selected = CreatePostType.DUEL },
                preview = { DuelPreview() }
            )

            PollTypeChoiceCard(
                title = stringResource(Res.string.create_post_type_yes_no_title),
                description = stringResource(Res.string.create_post_type_yes_no_desc),
                tag = "Oui / Non",
                selected = selected == CreatePostType.YES_NO,
                onClick = { selected = CreatePostType.YES_NO },
                preview = { YesNoPreview() }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onContinueClick(selected) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.create_post_type_continue))
            }
        }
    }
}
