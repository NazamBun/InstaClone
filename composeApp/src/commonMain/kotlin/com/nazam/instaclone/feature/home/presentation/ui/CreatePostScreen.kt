package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.home.domain.model.VoteCategories
import com.nazam.instaclone.feature.home.presentation.model.CreatePostUiState
import com.nazam.instaclone.feature.home.presentation.ui.components.NetworkImage
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.create_post_category_label
import instaclone.composeapp.generated.resources.create_post_choose_category_placeholder
import instaclone.composeapp.generated.resources.create_post_left_image_cd
import instaclone.composeapp.generated.resources.create_post_left_label
import instaclone.composeapp.generated.resources.create_post_publish_short
import instaclone.composeapp.generated.resources.create_post_question_label
import instaclone.composeapp.generated.resources.create_post_right_image_cd
import instaclone.composeapp.generated.resources.create_post_right_label
import instaclone.composeapp.generated.resources.create_post_title
import instaclone.composeapp.generated.resources.nav_back_cd
import org.jetbrains.compose.resources.stringResource

/**
 * UI only.
 * ✅ KMP friendly
 * ✅ Design "pro" : top bar + cards + spacing
 */
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
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scroll = rememberScrollState()

    val leftPreview = ui.leftUploadedUrl.ifBlank { ui.leftLocalUri }
    val rightPreview = ui.rightUploadedUrl.ifBlank { ui.rightLocalUri }
    val categoryText = if (ui.category.isBlank()) {
        stringResource(Res.string.create_post_choose_category_placeholder)
    } else {
        VoteCategories.labelFor(ui.category).asString()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.create_post_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.nav_back_cd)
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = onSubmitClick,
                        enabled = ui.isSubmitEnabled
                    ) { Text(stringResource(Res.string.create_post_publish_short)) }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scroll)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = ui.question,
                onValueChange = onQuestionChange,
                label = { Text(stringResource(Res.string.create_post_question_label)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(Modifier.height(14.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MediaCard(
                    title = stringResource(Res.string.create_post_left_label),
                    previewUrl = leftPreview,
                    isUploading = ui.isUploadingLeft,
                    onClick = onPickLeftImageClick,
                    contentDescription = stringResource(Res.string.create_post_left_image_cd),
                    modifier = Modifier.weight(1f)
                )
                MediaCard(
                    title = stringResource(Res.string.create_post_right_label),
                    previewUrl = rightPreview,
                    isUploading = ui.isUploadingRight,
                    onClick = onPickRightImageClick,
                    contentDescription = stringResource(Res.string.create_post_right_image_cd),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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

            Spacer(Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                tonalElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !ui.isLoading) { onChooseCategoryClick() }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.create_post_category_label),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(Modifier.width(10.dp))
                    AssistChip(
                        onClick = onChooseCategoryClick,
                        enabled = !ui.isLoading,
                        label = { Text(categoryText, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "›",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            ui.error?.let {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = it.asString(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            } ?: run {
                if (!ui.isSubmitEnabled && ui.submitBlockedReason != null) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = ui.submitBlockedReason.asString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            if (ui.isLoading) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) { CircularProgressIndicator() }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun MediaCard(
    title: String,
    previewUrl: String,
    isUploading: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)

    Surface(
        shape = shape,
        tonalElevation = 1.dp,
        modifier = modifier
            .aspectRatio(1f)
            .clickable(enabled = !isUploading) { onClick() }
    ) {
        Box(Modifier.fillMaxSize()) {
            if (previewUrl.isNotBlank()) {
                NetworkImage(
                    url = previewUrl,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize().clip(shape)
                )
            } else {
                Column(
                    Modifier.fillMaxSize().padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                    Spacer(Modifier.height(8.dp))
                    Text(title, style = MaterialTheme.typography.labelLarge)
                }
            }

            if (isUploading) {
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                    modifier = Modifier.fillMaxSize()
                ) {}
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
