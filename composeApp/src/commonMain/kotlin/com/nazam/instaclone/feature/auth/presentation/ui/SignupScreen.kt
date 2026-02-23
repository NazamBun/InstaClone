package com.nazam.instaclone.feature.auth.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.ui.AppTopBar
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.auth.presentation.model.SignupUiState
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.signup_button
import instaclone.composeapp.generated.resources.signup_display_name_placeholder
import instaclone.composeapp.generated.resources.signup_email_placeholder
import instaclone.composeapp.generated.resources.signup_go_to_login_button
import instaclone.composeapp.generated.resources.signup_password_placeholder
import instaclone.composeapp.generated.resources.signup_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignupScreen(
    ui: SignupUiState,
    onBackClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onDisplayNameChange: (String) -> Unit,
    onSignupClick: () -> Unit,
    onGoToLoginClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        AppTopBar(
            title = stringResource(Res.string.signup_title),
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = ui.email,
                onValueChange = onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(Res.string.signup_email_placeholder)) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = ui.password,
                onValueChange = onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(Res.string.signup_password_placeholder)) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = ui.displayName,
                onValueChange = onDisplayNameChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(Res.string.signup_display_name_placeholder)) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onSignupClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !ui.isLoading
            ) {
                Text(stringResource(Res.string.signup_button))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onGoToLoginClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !ui.isLoading
            ) {
                Text(stringResource(Res.string.signup_go_to_login_button))
            }

            ui.error?.let { err ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = err.asString(), color = Color.Red)
            }
        }
    }
}