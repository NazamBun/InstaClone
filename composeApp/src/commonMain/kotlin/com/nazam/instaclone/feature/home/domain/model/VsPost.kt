package com.nazam.instaclone.feature.home.domain.model

data class VsPost(
    val id: String,
    val authorId: String?,
    val authorName: String,
    val authorAvatarUrl: String?,
    val category: String,
    val createdAt: Long,
    val question: String,
    val leftImageUrl: String,
    val rightImageUrl: String,
    val leftLabel: String,
    val rightLabel: String,
    val leftVotesCount: Int,
    val rightVotesCount: Int,
    val totalVotesCount: Int,
    val commentsCount: Int = 0,
    val selectedVsBadgeId: String = "",
    val userVote: VoteChoice = VoteChoice.NONE
)
