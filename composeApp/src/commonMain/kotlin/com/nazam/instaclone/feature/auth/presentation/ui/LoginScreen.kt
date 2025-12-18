package com.nazam.instaclone.feature.auth.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.auth.presentation.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onNavigateToSignup: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val viewModel = remember { LoginViewModel() }
    val ui = viewModel.uiState.collectAsState()

    // ✅ Navigation propre (1 seule fois)
    LaunchedEffect(ui.value.isLoggedIn) {
        if (ui.value.isLoggedIn) {
            onNavigateToHome()
        }
    }

    Column(
        modifier = Modifier.padding(24.dp)
    ) {

        TextField(
            value = ui.value.email,
            onValueChange = { viewModel.onEmailChanged(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Email") }
        )

        TextField(
            value = ui.value.password,
            onValueChange = { viewModel.onPasswordChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            placeholder = { Text("Mot de passe") }
        )

        Button(
            onClick = { viewModel.login() },
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
            enabled = !ui.value.isLoading
        ) {
            if (ui.value.isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Connexion")
            }
        }

        Button(
            onClick = onNavigateToSignup,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Créer un compte")
        }

        if (ui.value.errorMessage != null) {
            Text(
                text = ui.value.errorMessage!!,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}