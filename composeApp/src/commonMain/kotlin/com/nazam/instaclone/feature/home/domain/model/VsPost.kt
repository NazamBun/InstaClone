package com.nazam.instaclone.feature.home.domain.model

// ✅ Type clair pour le vote de l'utilisateur
enum class VoteChoice {
    LEFT,
    RIGHT,
    NONE
}

data class VsPost(
    val id: String,
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

    // ✅ 1 seule source de vérité pour le vote user
    val userVote: VoteChoice = VoteChoice.NONE
)