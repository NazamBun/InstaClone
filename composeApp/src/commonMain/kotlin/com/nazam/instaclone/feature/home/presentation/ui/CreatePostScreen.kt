package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.presentation.viewmodel.CreatePostViewModel

@Composable
fun CreatePostScreen(
    viewModel: CreatePostViewModel,
    onBack: () -> Unit,
    onPostCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ui by viewModel.uiState.collectAsState()

    LaunchedEffect(ui.isPostCreated) {
        if (ui.isPostCreated) {
            viewModel.consumePostCreatedFlag()
            onPostCreated()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Créer un nouveau VS",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = ui.question,
                onValueChange = viewModel::onQuestionChange,
                label = { Text("Question") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.leftLabel,
                onValueChange = viewModel::onLeftLabelChange,
                label = { Text("Label gauche (ex: Montagne)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.rightLabel,
                onValueChange = viewModel::onRightLabelChange,
                label = { Text("Label droite (ex: Plage)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.leftImageUrl,
                onValueChange = viewModel::onLeftImageUrlChange,
                label = { Text("URL image gauche") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.rightImageUrl,
                onValueChange = viewModel::onRightImageUrlChange,
                label = { Text("URL image droite") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.category,
                onValueChange = viewModel::onCategoryChange,
                label = { Text("Catégorie (ex: Voyage)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (ui.errorMessage != null) {
                Text(
                    text = ui.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = { viewModel.submitPost() },
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Publier le VS")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onBack,
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Annuler")
            }
        }

        if (ui.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}