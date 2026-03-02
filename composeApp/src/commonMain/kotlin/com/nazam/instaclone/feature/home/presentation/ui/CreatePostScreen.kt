package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.home.domain.model.VoteCategories
import com.nazam.instaclone.feature.home.presentation.model.CreatePostUiState
import com.nazam.instaclone.feature.home.presentation.ui.components.NetworkImage
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.create_post_cancel
import instaclone.composeapp.generated.resources.create_post_category_label
import instaclone.composeapp.generated.resources.create_post_choose_category_button
import instaclone.composeapp.generated.resources.create_post_choose_category_placeholder
import instaclone.composeapp.generated.resources.create_post_left_label
import instaclone.composeapp.generated.resources.create_post_question_label
import instaclone.composeapp.generated.resources.create_post_right_label
import instaclone.composeapp.generated.resources.create_post_submit
import instaclone.composeapp.generated.resources.create_post_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    ui: CreatePostUiState,
    onQuestionChange: (String) -> Unit,
    onLeftLabelChange: (String) -> Unit,
    onRightLabelChange: (String) -> Unit,
    onPickLeftImageClick: () -> Unit,
    onPickRightImageClick: () -> Unit,
    onChooseCategoryClick: () -> Unit,
    onSubmitClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scroll = rememberScrollState()
    val leftPreview = if (ui.leftUploadedUrl.isNotBlank()) ui.leftUploadedUrl else ui.leftLocalUri
    val rightPreview = if (ui.rightUploadedUrl.isNotBlank()) ui.rightUploadedUrl else ui.rightLocalUri

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

            val categoryLabel = VoteCategories.labelFor(ui.category).asString()
            OutlinedTextField(
                value = if (ui.category.isBlank()) "" else categoryLabel,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(Res.string.create_post_category_label)) },
                placeholder = { Text(stringResource(Res.string.create_post_choose_category_placeholder)) },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onChooseCategoryClick,
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(Res.string.create_post_choose_category_button)) }

            ui.error?.let {
                Text(text = it.asString(), color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = onSubmitClick,
                enabled = ui.isSubmitEnabled,
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(Res.string.create_post_submit)) }

            OutlinedButton(
                onClick = onCancelClick,
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(Res.string.create_post_cancel)) }

            if (ui.isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) { CircularProgressIndicator() }
            }
        }
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
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Image $title", style = MaterialTheme.typography.titleMedium)

            if (preview.isNotBlank()) {
                NetworkImage(
                    url = preview,
                    contentDescription = "Image $title",
                    modifier = Modifier.fillMaxWidth().height(180.dp)
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    contentAlignment = Alignment.Center
                ) { Text("Choisir une photo") }
            }

            if (isUploading) {
                val progress = (percent ?: 0) / 100f
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                Text(text = if (percent != null) "$percent%" else "Upload…", style = MaterialTheme.typography.labelSmall)
            }

            Button(
                onClick = onPickClick,
                enabled = !isUploading,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (preview.isBlank()) "Choisir" else "Modifier") }
        }
    }
}
