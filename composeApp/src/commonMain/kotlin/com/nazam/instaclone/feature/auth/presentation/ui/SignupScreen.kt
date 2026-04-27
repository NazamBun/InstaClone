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
import com.nazam.instaclone.feature.auth.presentation.model.SignupUiState
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.signup_button
import instaclone.composeapp.generated.resources.signup_confirm_password_placeholder
import instaclone.composeapp.generated.resources.signup_display_name_placeholder
import instaclone.composeapp.generated.resources.signup_email_placeholder
import instaclone.composeapp.generated.resources.signup_go_to_login_button
import instaclone.composeapp.generated.resources.signup_password_placeholder
import instaclone.composeapp.generated.resources.signup_subtitle
import instaclone.composeapp.generated.resources.signup_title
import org.jetbrains.compose.resources.stringResource

private val FieldSpacing = 12.dp
private val SectionSpacing = 18.dp
private val ButtonSpacing = 10.dp
private val BottomPadding = 24.dp

@Composable
fun SignupScreen(
    ui: SignupUiState,
    onBackClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onDisplayNameChange: (String) -> Unit,
    onSignupClick: () -> Unit,
    onGoToLoginClick: () -> Unit
) {
    AuthBackground {
        Column(modifier = Modifier.fillMaxSize().imePadding()) {
            AuthTopBar(title = stringResource(Res.string.signup_title), onBackClick = onBackClick)
            Column(
                modifier = Modifier.fillMaxSize().padding(bottom = BottomPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AuthCard(
                    title = stringResource(Res.string.signup_title),
                    subtitle = stringResource(Res.string.signup_subtitle)
                ) {
                    AuthTextField(
                        value = ui.email,
                        onValueChange = onEmailChange,
                        placeholder = stringResource(Res.string.signup_email_placeholder)
                    )
                    Spacer(Modifier.height(FieldSpacing))
                    AuthTextField(
                        value = ui.password,
                        onValueChange = onPasswordChange,
                        placeholder = stringResource(Res.string.signup_password_placeholder),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(Modifier.height(FieldSpacing))
                    AuthTextField(
                        value = ui.confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        placeholder = stringResource(Res.string.signup_confirm_password_placeholder),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(Modifier.height(FieldSpacing))
                    AuthTextField(
                        value = ui.displayName,
                        onValueChange = onDisplayNameChange,
                        placeholder = stringResource(Res.string.signup_display_name_placeholder)
                    )
                    Spacer(Modifier.height(SectionSpacing))
                    AuthPrimaryButton(
                        label = stringResource(Res.string.signup_button),
                        loading = ui.isLoading,
                        onClick = onSignupClick
                    )
                    Spacer(Modifier.height(ButtonSpacing))
                    AuthSecondaryButton(
                        label = stringResource(Res.string.signup_go_to_login_button),
                        enabled = !ui.isLoading,
                        onClick = onGoToLoginClick
                    )
                    ui.error?.let { AuthErrorText(it.asString()) }
                }
            }
        }
    }
}
