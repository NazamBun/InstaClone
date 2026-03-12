package com.nazam.instaclone.feature.home.presentation.ui.explore

import com.nazam.instaclone.feature.home.domain.model.VsPost
import kotlin.math.abs
import kotlin.math.max

enum class ExploreSortMode {
    HOT,
    RECENT,
    CONTROVERSIAL
}

fun sortExplorePosts(posts: List<VsPost>, mode: ExploreSortMode): List<VsPost> {
    return when (mode) {
        ExploreSortMode.HOT -> posts.sortedWith(
            compareByDescending<VsPost> { it.totalVotesCount }
                .thenByDescending { it.createdAt }
        )

        ExploreSortMode.RECENT -> posts.sortedByDescending { it.createdAt }

        ExploreSortMode.CONTROVERSIAL -> posts.sortedWith(
            compareBy<VsPost> { controversyGap(it) }
                .thenByDescending { it.totalVotesCount }
                .thenByDescending { it.createdAt }
        )
    }
}

private fun controversyGap(post: VsPost): Int {
    val total = max(post.totalVotesCount, 1)
    val diff = abs(post.leftVotesCount - post.rightVotesCount)
    return (diff * 100) / total
}
