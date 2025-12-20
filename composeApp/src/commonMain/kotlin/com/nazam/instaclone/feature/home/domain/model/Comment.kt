package com.nazam.instaclone.feature.home.domain.model

data class Comment(
    val id: String,
    val postId: String,
    val authorId: String,
    val content: String,
    val authorName: String?,
    val authorAvatarUrl: String?,
    val createdAt: String // simple pour l’instant
)