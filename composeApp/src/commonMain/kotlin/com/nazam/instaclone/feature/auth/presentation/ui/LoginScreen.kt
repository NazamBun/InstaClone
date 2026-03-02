package com.nazam.instaclone.feature.auth.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.core.ui.AuthBackground
import com.nazam.instaclone.core.ui.AuthCard
import com.nazam.instaclone.core.ui.AuthErrorText
import com.nazam.instaclone.core.ui.AuthPrimaryButton
import com.nazam.instaclone.core.ui.AuthSecondaryButton
import com.nazam.instaclone.core.ui.AuthTextField
import com.nazam.instaclone.core.ui.AuthTopBar
import com.nazam.instaclone.core.ui.asString
import com.nazam.instaclone.feature.auth.presentation.model.LoginUiState
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.login_button
import instaclone.composeapp.generated.resources.login_email_placeholder
import instaclone.composeapp.generated.resources.login_go_to_signup_button
import instaclone.composeapp.generated.resources.login_password_placeholder
import instaclone.composeapp.generated.resources.login_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(
    ui: LoginUiState,
    onBackClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit
) {
    AuthBackground {
        Column(modifier = Modifier.fillMaxSize().imePadding()) {

            AuthTopBar(
                title = stringResource(Res.string.login_title),
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AuthCard(
                    title = stringResource(Res.string.login_title),
                    subtitle = "Reviens voter et poster tes VS."
                ) {
                    AuthTextField(
                        value = ui.email,
                        onValueChange = onEmailChange,
                        placeholder = stringResource(Res.string.login_email_placeholder)
                    )

                    Spacer(Modifier.height(12.dp))

                    AuthTextField(
                        value = ui.password,
                        onValueChange = onPasswordChange,
                        placeholder = stringResource(Res.string.login_password_placeholder),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Spacer(Modifier.height(18.dp))

                    AuthPrimaryButton(
                        label = stringResource(Res.string.login_button),
                        loading = ui.isLoading,
                        onClick = onLoginClick
                    )

                    Spacer(Modifier.height(10.dp))

                    AuthSecondaryButton(
                        label = stringResource(Res.string.login_go_to_signup_button),
                        enabled = !ui.isLoading,
                        onClick = onSignupClick
                    )

                    ui.error?.let { AuthErrorText(it.asString()) }
                }
            }
        }
    }
}
