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
 * - Accueil
 * - Filtrer
 * - Créer
 * - Se connecter / Déco
 *
 * ✅ Pas d'icônes
 * ✅ La surbrillance dépend de selectedItem
 */
@Composable
fun HomeBottomBar(
    isLoggedIn: Boolean,
    selectedItem: String, // "home" | "filter" | "create" | "auth"
    onHomeClick: () -> Unit,
    onFilterClick: () -> Unit,
    onCreatePostClick: () -> Unit,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = Color(0xFFFF4EB8)
    val normal = Color.White
    val muted = Color(0xFFBBBBBB)

    fun colorFor(item: String, inactiveColor: Color = normal): Color {
        return if (selectedItem == item) accent else inactiveColor
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Accueil",
            color = colorFor("home"),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selectedItem == "home") FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .clickable { onHomeClick() }
        )

        Text(
            text = "Filtrer",
            color = colorFor("filter"),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selectedItem == "filter") FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .clickable { onFilterClick() }
        )

        Text(
            text = "Créer",
            color = colorFor("create"),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selectedItem == "create") FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .clickable { onCreatePostClick() }
        )

        if (isLoggedIn) {
            Text(
                text = "Profil", // tu voulais remplacer "Connexion" par "Profil" plus tard
                color = colorFor("auth", inactiveColor = muted),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selectedItem == "auth") FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onLogoutClick() } // pour l’instant tu gardes logout ici
            )
        } else {
            Text(
                text = "Profil",
                color = colorFor("auth", inactiveColor = muted),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selectedItem == "auth") FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onLoginClick() }
            )
        }
    }
}