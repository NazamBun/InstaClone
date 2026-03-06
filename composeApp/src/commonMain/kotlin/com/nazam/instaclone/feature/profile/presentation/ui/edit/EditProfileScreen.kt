package com.nazam.instaclone.feature.profile.presentation.ui.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.profile.presentation.ui.ProfileUiTokens

@Composable
fun EditProfileScreen(
    contentPadding: PaddingValues,
    state: EditProfileUiState,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onDisplayNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onWebsiteChange: (String) -> Unit
) {
    val t = ProfileUiTokens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onBack) { Text("Retour") }
            Button(onClick = onSave, enabled = !state.isSaving) {
                Text(if (state.isSaving) "Sauvegarde..." else "Sauvegarder")
            }
        }

        if (state.isLoading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator()
            return
        }

        state.error?.let {
            Spacer(Modifier.height(12.dp))
            Text(text = it.asString(), color = t.Accent)
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.displayName,
            onValueChange = onDisplayNameChange,
            label = { Text("Nom affiché") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = state.username,
            onValueChange = onUsernameChange,
            label = { Text("Pseudo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = state.bio,
            onValueChange = onBioChange,
            label = { Text("Bio") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = state.location,
            onValueChange = onLocationChange,
            label = { Text("Ville") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = state.website,
            onValueChange = onWebsiteChange,
            label = { Text("Site") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
