package com.nazam.instaclone.feature.home.presentation.ui.explore

import com.nazam.instaclone.feature.home.domain.model.VsPost
import kotlin.math.abs

/**
 * Types de tri possibles pour Explore.
 * ✅ simple
 * ✅ KMP friendly
 */
enum class ExploreSortMode {
    HOT,            // plus de votes en premier
    RECENT,         // plus récent en premier
    CONTROVERSIAL   // votes serrés (ex: 51/49) en premier
}

/**
 * Fonction PURE : pas de Compose, pas d'IO, pas de date "now".
 * Elle prend une liste -> retourne une nouvelle liste triée.
 *
 * ✅ Facile à tester
 * ✅ KMP friendly
 */
fun sortExplorePosts(
    posts: List<VsPost>,
    mode: ExploreSortMode
): List<VsPost> {
    return when (mode) {
        ExploreSortMode.HOT ->
            posts.sortedByDescending { it.totalVotesCount }

        ExploreSortMode.RECENT ->
            posts.sortedByDescending { it.createdAt }

        ExploreSortMode.CONTROVERSIAL ->
            posts.sortedWith(
                compareBy<VsPost> { voteGap(it) } // plus petit écart d'abord (serré)
                    .thenByDescending { it.totalVotesCount } // si égal, plus de votes d'abord
            )
    }
}

/**
 * Écart entre les 2 camps.
 * Exemple: 50/50 => gap 0 (très controversé)
 */
private fun voteGap(post: VsPost): Int {
    return abs(post.leftVotesCount - post.rightVotesCount)
}