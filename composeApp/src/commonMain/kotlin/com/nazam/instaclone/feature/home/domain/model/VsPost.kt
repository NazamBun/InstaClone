package com.nazam.instaclone.feature.home.domain.model

data class VsPost(
    val id: String,
    val authorName: String,
    val authorAvatarUrl: String?,
    val category: String,
    val createdAt: Long,
    val question: String,          // "Que choisissez-vous pour le déjeuner ?"
    val leftImageUrl: String,
    val rightImageUrl: String,
    val leftLabel: String,         // "Burger"
    val rightLabel: String,        // "Salade"
    val leftVotesCount: Int,
    val rightVotesCount: Int,
    val totalVotesCount: Int,
    val isVotedLeft: Boolean,
    val isVotedRight: Boolean
)