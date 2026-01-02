package com.nazam.instaclone.feature.home.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Bottom bar simple (KMP friendly) :
 * - Accueil : enlève le filtre
 * - Filtrer : ouvre Categories
 * - Créer : va vers CreatePost
 * - Se connecter / Déco
 *
 * ✅ Pas d'icônes
 * ✅ 4 zones égales (propre)
 */
@Composable
fun HomeBottomBar(
    isLoggedIn: Boolean,
    onHomeClick: () -> Unit,
    onFilterClick: () -> Unit,
    onCreatePostClick: () -> Unit,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Accueil",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .clickable { onHomeClick() }
        )

        Text(
            text = "Filtrer",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .clickable { onFilterClick() }
        )

        Text(
            text = "Créer",
            color = Color(0xFFFF4EB8),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .clickable { onCreatePostClick() }
        )

        if (isLoggedIn) {
            Text(
                text = "Déco",
                color = Color(0xFFBBBBBB),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onLogoutClick() }
            )
        } else {
            Text(
                text = "Se connecter",
                color = Color(0xFFBBBBBB),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onLoginClick() }
            )
        }
    }
}