package com.nazam.instaclone.feature.home.presentation.ui.createpostyesno

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.home.presentation.model.CreatePostYesNoUiState
import com.nazam.instaclone.feature.home.presentation.ui.components.NetworkImage
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.create_post_hashtag_hint
import instaclone.composeapp.generated.resources.create_post_question_label
import instaclone.composeapp.generated.resources.create_post_yes_no_change_image_cta
import instaclone.composeapp.generated.resources.create_post_yes_no_image_label
import instaclone.composeapp.generated.resources.create_post_yes_no_pick_image_cta
import instaclone.composeapp.generated.resources.create_post_yes_no_publish_cta
import instaclone.composeapp.generated.resources.create_post_yes_no_title
import instaclone.composeapp.generated.resources.create_post_yes_no_uploading_label
import instaclone.composeapp.generated.resources.nav_back_cd
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostYesNoScreen(
    ui: CreatePostYesNoUiState,
    onBackClick: () -> Unit,
    onQuestionChange: (String) -> Unit,
    onPickImageClick: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text(stringResource(Res.string.create_post_yes_no_title)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(Res.string.nav_back_cd),
                    )
                }
            }
        )
        if (ui.authBlocked) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            CreateYesNoForm(
                ui = ui,
                onQuestionChange = onQuestionChange,
                onPickImageClick = onPickImageClick,
                onSubmitClick = onSubmitClick
            )
        }
    }
}

@Composable
private fun CreateYesNoForm(
    ui: CreatePostYesNoUiState,
    onQuestionChange: (String) -> Unit,
    onPickImageClick: () -> Unit,
    onSubmitClick: () -> Unit
) {
    val preview = ui.imageUploadedUrl.ifBlank { ui.imageLocalUri }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = ui.question,
            onValueChange = onQuestionChange,
            label = { Text(stringResource(Res.string.create_post_question_label)) },
            supportingText = { Text(stringResource(Res.string.create_post_hashtag_hint)) },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(Res.string.create_post_yes_no_image_label),
            style = MaterialTheme.typography.titleMedium
        )
        ImageBackgroundCard(
            preview = preview,
            isUploading = ui.isUploadingImage,
            percent = ui.imageUploadPercent,
            onPickClick = onPickImageClick
        )
        ui.error?.let { Text(text = it.asString(), color = MaterialTheme.colorScheme.error) }
        Button(
            onClick = onSubmitClick,
            enabled = ui.submitBlockedReason == null && !ui.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) { Text(stringResource(Res.string.create_post_yes_no_publish_cta)) }
        if (ui.isLoading) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun ImageBackgroundCard(
    preview: String,
    isUploading: Boolean,
    percent: Int?,
    onPickClick: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (preview.isNotBlank()) {
                NetworkImage(
                    url = preview,
                    contentDescription = stringResource(Res.string.create_post_yes_no_image_label),
                    modifier = Modifier.fillMaxWidth().height(220.dp)
                )
            } else {
                Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                    Text(stringResource(Res.string.create_post_yes_no_pick_image_cta))
                }
            }
            if (isUploading) {
                LinearProgressIndicator(
                    progress = { (percent ?: 0) / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(Res.string.create_post_yes_no_uploading_label),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Button(
                onClick = onPickClick,
                enabled = !isUploading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(
                    if (preview.isBlank()) Res.string.create_post_yes_no_pick_image_cta
                    else Res.string.create_post_yes_no_change_image_cta
                ))
            }
        }
    }
}
