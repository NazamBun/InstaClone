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
import com.nazam.instaclone.feature.home.domain.model.VoteCategories
import com.nazam.instaclone.feature.home.presentation.model.CreatePostUiState
import com.nazam.instaclone.feature.home.presentation.ui.components.NetworkImage

/**
 * UI only.
 *
 * ✅ Scrollable : on peut scroller et voir les boutons
 * ✅ Upload direct : on montre "upload en cours"
 * ✅ Etape 2 : le ViewModel nous dit si on peut publier + pourquoi sinon
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

    // ✅ preview : si URL uploadée existe -> on affiche ça, sinon URI locale
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
                text = Strings.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = ui.question,
                onValueChange = onQuestionChange,
                label = { Text(Strings.question) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.leftLabel,
                onValueChange = onLeftLabelChange,
                label = { Text(Strings.leftLabel) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.rightLabel,
                onValueChange = onRightLabelChange,
                label = { Text(Strings.rightLabel) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ Image gauche
            Button(
                onClick = onPickLeftImageClick,
                enabled = !ui.isLoading && !ui.isUploadingLeft,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (ui.leftLocalUri.isBlank()) Strings.pickLeft else Strings.changeLeft)
            }

            if (ui.isUploadingLeft) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(Strings.uploadingLeft, style = MaterialTheme.typography.bodySmall)
                }
            }

            if (leftPreview.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                NetworkImage(
                    url = leftPreview,
                    contentDescription = Strings.leftImage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ Image droite
            Button(
                onClick = onPickRightImageClick,
                enabled = !ui.isLoading && !ui.isUploadingRight,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (ui.rightLocalUri.isBlank()) Strings.pickRight else Strings.changeRight)
            }

            if (ui.isUploadingRight) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(Strings.uploadingRight, style = MaterialTheme.typography.bodySmall)
                }
            }

            if (rightPreview.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                NetworkImage(
                    url = rightPreview,
                    contentDescription = Strings.rightImage,
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
                label = { Text(Strings.category) },
                placeholder = { Text(Strings.chooseCategoryPlaceholder) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onChooseCategoryClick,
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(Strings.chooseCategory)
            }

            // ✅ Erreur (vraie erreur)
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
                enabled = ui.isSubmitEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(Strings.submit)
            }

            // ✅ Info si bouton bloqué (raison simple)
            if (!ui.isSubmitEnabled && ui.submitBlockedReason != null && ui.errorMessage == null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = ui.submitBlockedReason,
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
                Text(Strings.cancel)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (ui.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

private object Strings {
    const val title = "Créer un nouveau VS"
    const val question = "Question"
    const val leftLabel = "Label gauche"
    const val rightLabel = "Label droite"

    const val pickLeft = "Choisir image gauche"
    const val changeLeft = "Changer image gauche"
    const val pickRight = "Choisir image droite"
    const val changeRight = "Changer image droite"

    const val uploadingLeft = "Upload image gauche..."
    const val uploadingRight = "Upload image droite..."

    const val leftImage = "Image gauche"
    const val rightImage = "Image droite"

    const val category = "Catégorie"
    const val chooseCategoryPlaceholder = "Choisir une catégorie"
    const val chooseCategory = "Choisir la catégorie"

    const val submit = "Publier le VS"
    const val cancel = "Annuler"
}