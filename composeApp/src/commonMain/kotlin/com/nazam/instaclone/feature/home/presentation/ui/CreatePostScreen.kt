package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.home.domain.model.VoteCategories
import com.nazam.instaclone.feature.home.presentation.model.CreatePostUiState
import com.nazam.instaclone.feature.home.presentation.ui.components.NetworkImage
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/**
 * UI only.
 * ✅ KMP friendly : strings via composeResources
 */
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
    val scrollState = rememberScrollState()

    val leftPreview = if (ui.leftUploadedUrl.isNotBlank()) ui.leftUploadedUrl else ui.leftLocalUri
    val rightPreview = if (ui.rightUploadedUrl.isNotBlank()) ui.rightUploadedUrl else ui.rightLocalUri

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = stringResource(Res.string.create_post_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = ui.question,
                onValueChange = onQuestionChange,
                label = { Text(stringResource(Res.string.create_post_question_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.leftLabel,
                onValueChange = onLeftLabelChange,
                label = { Text(stringResource(Res.string.create_post_left_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.rightLabel,
                onValueChange = onRightLabelChange,
                label = { Text(stringResource(Res.string.create_post_right_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onPickLeftImageClick,
                enabled = !ui.isLoading && !ui.isUploadingLeft,
                modifier = Modifier.fillMaxWidth()
            ) {
                val labelRes =
                    if (ui.leftLocalUri.isBlank()) Res.string.create_post_pick_left_image
                    else Res.string.create_post_change_left_image
                Text(stringResource(labelRes))
            }

            if (ui.isUploadingLeft) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.create_post_uploading_left),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (leftPreview.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                NetworkImage(
                    url = leftPreview,
                    contentDescription = stringResource(Res.string.create_post_left_image_cd),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onPickRightImageClick,
                enabled = !ui.isLoading && !ui.isUploadingRight,
                modifier = Modifier.fillMaxWidth()
            ) {
                val labelRes =
                    if (ui.rightLocalUri.isBlank()) Res.string.create_post_pick_right_image
                    else Res.string.create_post_change_right_image
                Text(stringResource(labelRes))
            }

            if (ui.isUploadingRight) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.create_post_uploading_right),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (rightPreview.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                NetworkImage(
                    url = rightPreview,
                    contentDescription = stringResource(Res.string.create_post_right_image_cd),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val categoryLabel = VoteCategories.labelFor(ui.category)

            OutlinedTextField(
                value = if (ui.category.isBlank()) "" else categoryLabel,
                onValueChange = { },
                readOnly = true,
                label = { Text(stringResource(Res.string.create_post_category_label)) },
                placeholder = { Text(stringResource(Res.string.create_post_choose_category_placeholder)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onChooseCategoryClick,
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.create_post_choose_category_button))
            }

            // ✅ Erreur UI (UiText)
            ui.error?.let { err ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = err.asString(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSubmitClick,
                enabled = ui.isSubmitEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.create_post_submit))
            }

            // ✅ Message “pourquoi c’est bloqué”
            if (!ui.isSubmitEnabled && ui.submitBlockedReason != null && ui.error == null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = ui.submitBlockedReason.asString(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onCancelClick,
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.create_post_cancel))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (ui.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}