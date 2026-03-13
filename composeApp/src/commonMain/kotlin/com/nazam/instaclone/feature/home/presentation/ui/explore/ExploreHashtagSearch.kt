package com.nazam.instaclone.feature.home.presentation.ui.explore

import com.nazam.instaclone.feature.home.domain.model.VsPost

private val hashtagRegex = Regex("""#([A-Za-z0-9_]+)""")

fun filterPostsByHashtag(
    posts: List<VsPost>,
    query: String
): List<VsPost> {
    val normalized = normalizeHashtagQuery(query)
    if (normalized.isBlank()) return posts

    return posts.filter { post ->
        extractHashtags(post).any { it == normalized || it.startsWith(normalized) }
    }
}

fun normalizeHashtagQuery(value: String): String {
    return value.trim().removePrefix("#").lowercase()
}

private fun extractHashtags(post: VsPost): Set<String> {
    val source = listOf(post.question, post.leftLabel, post.rightLabel).joinToString(" ")
    return hashtagRegex.findAll(source)
        .map { it.groupValues[1].lowercase() }
        .toSet()
}
