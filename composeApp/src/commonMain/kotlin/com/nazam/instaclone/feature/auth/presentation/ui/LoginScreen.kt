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
import instaclone.composeapp.generated.resources.login_subtitle
import instaclone.composeapp.generated.resources.login_title
import org.jetbrains.compose.resources.stringResource

private val FieldSpacing = 12.dp
private val SectionSpacing = 18.dp
private val ButtonSpacing = 10.dp
private val BottomPadding = 24.dp

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
            AuthTopBar(title = stringResource(Res.string.login_title), onBackClick = onBackClick)
            Column(
                modifier = Modifier.fillMaxSize().padding(bottom = BottomPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AuthCard(
                    title = stringResource(Res.string.login_title),
                    subtitle = stringResource(Res.string.login_subtitle)
                ) {
                    AuthTextField(
                        value = ui.email,
                        onValueChange = onEmailChange,
                        placeholder = stringResource(Res.string.login_email_placeholder)
                    )
                    Spacer(Modifier.height(FieldSpacing))
                    AuthTextField(
                        value = ui.password,
                        onValueChange = onPasswordChange,
                        placeholder = stringResource(Res.string.login_password_placeholder),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(Modifier.height(SectionSpacing))
                    AuthPrimaryButton(
                        label = stringResource(Res.string.login_button),
                        loading = ui.isLoading,
                        onClick = onLoginClick
                    )
                    Spacer(Modifier.height(ButtonSpacing))
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
