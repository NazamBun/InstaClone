package com.nazam.instaclone.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AuthBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AuthUiTokens.backgroundBrush())
    ) {
        content()
    }
}

@Composable
fun AuthTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "←",
            color = AuthUiTokens.TextPrimary,
            modifier = Modifier
                .clickable { onBackClick() }
                .padding(8.dp)
        )

        Spacer(Modifier.width(6.dp))

        Text(
            text = title,
            color = AuthUiTokens.TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AuthCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(
                width = 1.dp,
                color = AuthUiTokens.Accent.copy(alpha = 0.25f),
                shape = RoundedCornerShape(AuthUiTokens.CardRadius)
            )
            .background(AuthUiTokens.Card, RoundedCornerShape(AuthUiTokens.CardRadius))
            .padding(18.dp)
    ) {
        Text(text = title, color = AuthUiTokens.TextPrimary, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(text = subtitle, color = AuthUiTokens.TextSecondary)
        Spacer(Modifier.height(16.dp))
        content()
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = AuthUiTokens.TextSecondary) },
        singleLine = true,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = AuthUiTokens.TextPrimary,
            unfocusedTextColor = AuthUiTokens.TextPrimary,
            cursorColor = AuthUiTokens.Accent,
            focusedBorderColor = AuthUiTokens.Accent,
            unfocusedBorderColor = AuthUiTokens.TextSecondary.copy(alpha = 0.35f),
            focusedContainerColor = AuthUiTokens.Field,
            unfocusedContainerColor = AuthUiTokens.Field
        )
    )
}

@Composable
fun AuthPrimaryButton(
    label: String,
    loading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = !loading,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = AuthUiTokens.Accent,
            contentColor = AuthUiTokens.TextPrimary
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = AuthUiTokens.TextPrimary,
                modifier = Modifier.height(18.dp)
            )
        } else {
            Text(label, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AuthSecondaryButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = AuthUiTokens.TextPrimary
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = SolidColor(AuthUiTokens.TextSecondary.copy(alpha = 0.35f))
        )
    ) { Text(label) }
}

@Composable
fun AuthErrorText(message: String) {
    Text(
        text = message,
        color = AuthUiTokens.Accent,
        modifier = Modifier.padding(top = 10.dp)
    )
}
