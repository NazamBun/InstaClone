package com.nazam.instaclone.feature.profile.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.instaclone.feature.home.domain.model.VsPost

/**
 * Grille simple (sans LazyVerticalGrid)
 * - évite les problèmes de taille dans LazyColumn
 * - KMP friendly
 */
@Composable
fun ProfileGrid(
    posts: List<VsPost>,
    onPostClick: (VsPost) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

        val rows = posts.chunked(3)

        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { post ->
                    Box(modifier = Modifier.weight(1f)) {
                        ProfileGridItem(post = post, onClick = { onPostClick(post) })
                    }
                }

                // si la ligne n’a pas 3 items -> on complète avec des espaces
                repeat(3 - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(16.dp))
    }
}
