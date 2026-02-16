package com.nazam.instaclone.feature.home.presentation.ui.explore

import com.nazam.instaclone.feature.home.domain.model.VsPost
import kotlin.math.abs
import kotlin.math.max

/**
 * Modes de tri pour Explore.
 * ✅ Pur (pas de Compose)
 * ✅ Facile à tester
 */
enum class ExploreSortMode {
    HOT,
    RECENT,
    CONTROVERSIAL
}

/**
 * Fonction de tri pure.
 * - HOT : le plus de votes
 * - RECENT : le plus récent (createdAt)
 * - CONTROVERSIAL : votes serrés (diff faible), puis beaucoup de votes
 */
fun sortExplorePosts(
    posts: List<VsPost>,
    mode: ExploreSortMode
): List<VsPost> {
    return when (mode) {
        ExploreSortMode.HOT -> posts.sortedByDescending { it.totalVotesCount }

        ExploreSortMode.RECENT -> posts.sortedByDescending { it.createdAt }

        ExploreSortMode.CONTROVERSIAL -> posts.sortedWith(
            compareBy<VsPost>(
                // plus c'est petit, plus c'est "serré"
                { abs(it.leftVotesCount - it.rightVotesCount) }
            ).thenByDescending {
                // si c'est aussi serré, on préfère ceux avec beaucoup de votes
                it.totalVotesCount
            }
        )
    }
}

/**
 * Petit helper optionnel : calcule un score de "serré".
 * Plus c'est petit, plus c'est serré.
 * (Utile si tu veux tester facilement en unit test)
 */
internal fun controversyGap(post: VsPost): Int {
    val total = max(post.totalVotesCount, 1)
    // Le gap en % (approx) pour être plus stable si tu veux
    val diff = abs(post.leftVotesCount - post.rightVotesCount)
    return (diff * 100) / total
}