package com.nazam.instaclone.feature.home.domain.model

/**
 * Modèle de domaine utilisé par l’UI.
 * Indépendant de Supabase, Room, Retrofit, etc.
 */
data class VsPost(
    val id: String,

    // Auteur
    val authorName: String,
    val authorAvatarUrl: String?,

    // Contenu
    val category: String,
    val createdAt: Long,
    val question: String,

    // Images VS
    val leftImageUrl: String,
    val rightImageUrl: String,
    val leftLabel: String,
    val rightLabel: String,

    // Votes globaux
    val leftVotesCount: Int,
    val rightVotesCount: Int,
    val totalVotesCount: Int,

    // ✅ Vote de l’utilisateur courant
    val userVote: VoteChoice = VoteChoice.NONE
)