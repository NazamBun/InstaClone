package com.nazam.instaclone.feature.home.presentation.ui.createposttype

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.presentation.model.CreatePostType
import com.nazam.instaclone.feature.home.presentation.ui.createposttype.components.DuelPreview
import com.nazam.instaclone.feature.home.presentation.ui.createposttype.components.PollTypeChoiceCard
import com.nazam.instaclone.feature.home.presentation.ui.createposttype.components.YesNoPreview
import instaclone.composeapp.generated.resources.*
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.create_post_type_subtitle),
                style = MaterialTheme.typography.bodyLarge,
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
