package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.nazam.instaclone.feature.home.domain.model.VoteCategories
import com.nazam.instaclone.feature.home.presentation.model.CreatePostUiState
import com.nazam.instaclone.feature.home.presentation.ui.components.NetworkImage

/**
 * UI only.
 * Pas de ViewModel ici : juste état + callbacks.
 *
 * ✅ Scrollable : quand les images sont affichées, on peut scroller pour voir les boutons.
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
                .verticalScroll(scrollState) // ✅ scroll
        ) {
            Text(
                text = "Créer un nouveau VS",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = ui.question,
                onValueChange = onQuestionChange,
                label = { Text("Question") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.leftLabel,
                onValueChange = onLeftLabelChange,
                label = { Text("Label gauche") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.rightLabel,
                onValueChange = onRightLabelChange,
                label = { Text("Label droite") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ Image gauche
            Button(
                onClick = onPickLeftImageClick,
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (ui.leftImageUrl.isBlank()) "Choisir image gauche" else "Changer image gauche")
            }

            if (ui.leftImageUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                NetworkImage(
                    url = ui.leftImageUrl,
                    contentDescription = "Image gauche",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ Image droite
            Button(
                onClick = onPickRightImageClick,
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (ui.rightImageUrl.isBlank()) "Choisir image droite" else "Changer image droite")
            }

            if (ui.rightImageUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                NetworkImage(
                    url = ui.rightImageUrl,
                    contentDescription = "Image droite",
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
                label = { Text("Catégorie") },
                placeholder = { Text("Choisir une catégorie") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onChooseCategoryClick,
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Choisir la catégorie")
            }

            ui.errorMessage?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSubmitClick,
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publier le VS")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onCancelClick,
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Annuler")
            }

            // ✅ petit espace pour être sûr de voir les derniers boutons
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (ui.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
