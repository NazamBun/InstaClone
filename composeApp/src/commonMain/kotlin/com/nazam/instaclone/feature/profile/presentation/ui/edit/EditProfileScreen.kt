package com.nazam.instaclone.feature.profile.presentation.ui.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.profile.presentation.ui.ProfileUiTokens
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.edit_profile_back
import instaclone.composeapp.generated.resources.edit_profile_bio
import instaclone.composeapp.generated.resources.edit_profile_display_name
import instaclone.composeapp.generated.resources.edit_profile_location
import instaclone.composeapp.generated.resources.edit_profile_save
import instaclone.composeapp.generated.resources.edit_profile_saving
import instaclone.composeapp.generated.resources.edit_profile_username
import instaclone.composeapp.generated.resources.edit_profile_website
import org.jetbrains.compose.resources.stringResource

private val ScreenPadding = 16.dp
private val ButtonSpacing = 12.dp
private val FieldSpacing = 10.dp
private val SectionSpacing = 16.dp

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
    Column(
        modifier = Modifier.fillMaxSize().padding(contentPadding).padding(ScreenPadding)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(ButtonSpacing)) {
            OutlinedButton(onClick = onBack) {
                Text(stringResource(Res.string.edit_profile_back))
            }
            Button(onClick = onSave, enabled = !state.isSaving) {
                Text(stringResource(
                    if (state.isSaving) Res.string.edit_profile_saving
                    else Res.string.edit_profile_save
                ))
            }
        }

        if (state.isLoading) {
            Spacer(Modifier.height(SectionSpacing))
            CircularProgressIndicator()
            return
        }

        state.error?.let {
            Spacer(Modifier.height(ButtonSpacing))
            Text(text = it.asString(), color = ProfileUiTokens.Accent)
        }

        Spacer(Modifier.height(SectionSpacing))
        Field(value = state.displayName, onChange = onDisplayNameChange,
            label = stringResource(Res.string.edit_profile_display_name))
        Spacer(Modifier.height(FieldSpacing))
        Field(value = state.username, onChange = onUsernameChange,
            label = stringResource(Res.string.edit_profile_username))
        Spacer(Modifier.height(FieldSpacing))
        Field(value = state.bio, onChange = onBioChange,
            label = stringResource(Res.string.edit_profile_bio), minLines = 3)
        Spacer(Modifier.height(FieldSpacing))
        Field(value = state.location, onChange = onLocationChange,
            label = stringResource(Res.string.edit_profile_location))
        Spacer(Modifier.height(FieldSpacing))
        Field(value = state.website, onChange = onWebsiteChange,
            label = stringResource(Res.string.edit_profile_website))
    }
}

@Composable
private fun Field(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        minLines = minLines
    )
}
