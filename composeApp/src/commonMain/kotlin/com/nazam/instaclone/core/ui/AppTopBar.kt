package com.nazam.instaclone.core.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.nav_back_cd
import org.jetbrains.compose.resources.stringResource

/**
 * Petite TopBar KMP friendly.
 * - Pas de code Android-only
 * - Flèche retour + titre
 */
@Composable
fun AppTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = { androidx.compose.material3.Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(Res.string.nav_back_cd)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}