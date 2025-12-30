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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.auth.presentation.model.LoginUiState

/**
 * Ecran pur (UI only).
 * Il ne connaît pas Koin et ne connaît pas les events.
 */
@Composable
fun LoginScreen(
    ui: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Connexion")

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = ui.email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Email") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = ui.password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Mot de passe") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !ui.isLoading
        ) {
            if (ui.isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Connexion")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSignupClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !ui.isLoading
        ) {
            Text("Créer un compte")
        }

        ui.errorMessage?.let { msg ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = msg, color = Color.Red)
        }
    }
}