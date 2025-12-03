package com.nazam.instaclone.feature.auth.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
fun SignupScreen() {

    val viewModel = remember { SignupViewModel() }
    val ui = viewModel.uiState.collectAsState()

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

        TextField(
            value = ui.value.displayName,
            onValueChange = { viewModel.onDisplayNameChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            placeholder = { Text("Nom d'utilisateur") }
        )

        Button(
            onClick = { viewModel.signup() },
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
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

        if (ui.value.isSignedUp) {
            Text(
                text = "🎉 Compte créé ! Vérifie ton email.",
                color = Color.Green,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}