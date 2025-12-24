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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.auth.presentation.viewmodel.SignupViewModel

@Composable
fun SignupScreen(
    onNavigateToLogin: () -> Unit
) {
    val viewModel = remember { SignupViewModel() }
    val ui = viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "Créer un compte")

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = ui.value.email,
            onValueChange = viewModel::onEmailChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Email") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = ui.value.password,
            onValueChange = viewModel::onPasswordChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Mot de passe") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = ui.value.displayName,
            onValueChange = viewModel::onDisplayNameChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Nom d'utilisateur") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = viewModel::signup,
            modifier = Modifier.fillMaxWidth(),
            enabled = !ui.value.isLoading
        ) {
            Text("Créer un compte")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth(),
            enabled = !ui.value.isLoading
        ) {
            Text("Déjà un compte ? Se connecter")
        }

        ui.value.errorMessage?.let { msg ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = msg, color = Color.Red)
        }

        if (ui.value.isSignedUp) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "🎉 Compte créé ! Vérifie ton email.", color = Color(0xFF2E7D32))
        }
    }
}