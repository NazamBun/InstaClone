package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.home.presentation.model.CreatePostUiState
import com.nazam.instaclone.feature.home.presentation.ui.components.NetworkImage
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.create_post_cancel
import instaclone.composeapp.generated.resources.create_post_hashtag_hint
import instaclone.composeapp.generated.resources.create_post_left_label
import instaclone.composeapp.generated.resources.create_post_question_label
import instaclone.composeapp.generated.resources.create_post_right_label
import instaclone.composeapp.generated.resources.create_post_submit
import instaclone.composeapp.generated.resources.create_post_title
import instaclone.composeapp.generated.resources.vs2
import instaclone.composeapp.generated.resources.vs_1
import instaclone.composeapp.generated.resources.vsfuturistic
import instaclone.composeapp.generated.resources.vsor
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private data class VsBadgeOption(
    val id: String,
    val title: String,
    val drawable: DrawableResource
)

private val badgeOptions = listOf(
    VsBadgeOption("vs_1", "VS combat", Res.drawable.vs_1),
    VsBadgeOption("vs2", "VS classique", Res.drawable.vs2),
    VsBadgeOption("vsfuturistic", "VS futuriste", Res.drawable.vsfuturistic),
    VsBadgeOption("vsor", "VS premium", Res.drawable.vsor)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    ui: CreatePostUiState,
    onQuestionChange: (String) -> Unit,
    onLeftLabelChange: (String) -> Unit,
    onRightLabelChange: (String) -> Unit,
    onVsBadgeSelected: (String) -> Unit,
    onPickLeftImageClick: () -> Unit,
    onPickRightImageClick: () -> Unit,
    onSubmitClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scroll = rememberScrollState()
    val leftPreview = ui.leftUploadedUrl.ifBlank { ui.leftLocalUri }
    val rightPreview = ui.rightUploadedUrl.ifBlank { ui.rightLocalUri }

    Column(modifier = modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text(stringResource(Res.string.create_post_title)) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = ui.question,
                onValueChange = onQuestionChange,
                label = { Text(stringResource(Res.string.create_post_question_label)) },
                supportingText = { Text(stringResource(Res.string.create_post_hashtag_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = ui.leftLabel,
                    onValueChange = onLeftLabelChange,
                    label = { Text(stringResource(Res.string.create_post_left_label)) },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = ui.rightLabel,
                    onValueChange = onRightLabelChange,
                    label = { Text(stringResource(Res.string.create_post_right_label)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "Choisis un style de VS",
                style = MaterialTheme.typography.titleMedium
            )

            badgeOptions.forEach { option ->
                VsBadgeOptionRow(
                    title = option.title,
                    drawable = option.drawable,
                    selected = ui.selectedVsBadgeId == option.id,
                    onClick = { onVsBadgeSelected(option.id) }
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ImageCard(
                    title = "A",
                    preview = leftPreview,
                    isUploading = ui.isUploadingLeft,
                    percent = ui.leftUploadPercent,
                    onPickClick = onPickLeftImageClick,
                    modifier = Modifier.weight(1f)
                )
                ImageCard(
                    title = "B",
                    preview = rightPreview,
                    isUploading = ui.isUploadingRight,
                    percent = ui.rightUploadPercent,
                    onPickClick = onPickRightImageClick,
                    modifier = Modifier.weight(1f)
                )
            }

            ui.error?.let {
                Text(
                    text = it.asString(),
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = onSubmitClick,
                enabled = ui.isSubmitEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.create_post_submit))
            }

            OutlinedButton(
                onClick = onCancelClick,
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.create_post_cancel))
            }

            if (ui.isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun VsBadgeOptionRow(
    title: String,
    drawable: DrawableResource,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Spacer(modifier = Modifier.size(8.dp))

        Image(
            painter = painterResource(drawable),
            contentDescription = title,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.size(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ImageCard(
    title: String,
    preview: String,
    isUploading: Boolean,
    percent: Int?,
    onPickClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Image $title",
                style = MaterialTheme.typography.titleMedium
            )

            if (preview.isNotBlank()) {
                NetworkImage(
                    url = preview,
                    contentDescription = "Image $title",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Choisir une photo")
                }
            }

            if (isUploading) {
                LinearProgressIndicator(
                    progress = { (percent ?: 0) / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = if (percent != null) "$percent%" else "Upload…",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Button(
                onClick = onPickClick,
                enabled = !isUploading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (preview.isBlank()) "Choisir" else "Modifier")
            }
        }
    }
}
