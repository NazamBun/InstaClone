package com.nazam.instaclone.feature.home.data.mapper

import com.nazam.instaclone.feature.home.data.dto.PostDto
import com.nazam.instaclone.feature.home.domain.model.UserVote
import com.nazam.instaclone.feature.home.domain.model.VsPost

/**
 * Mapper entre PostDto (data layer)
 * et VsPost (domain layer)
 */
object PostMapper {

    /**
     * @param dto post venant de Supabase
     * @param userVote vote du user courant sur ce post (peut être null)
     */
    fun toDomain(
        dto: PostDto,
        userVote: UserVote?
    ): VsPost {

        val totalVotes = dto.left_votes + dto.right_votes

        return VsPost(
            id = dto.id,

            // Auteur
            authorName = dto.author_name ?: "Inconnu",
            authorAvatarUrl = dto.author_avatar,

            // Contenu
            category = dto.category ?: "",
            createdAt = 0L, // volontairement simplifié
            question = dto.question ?: "",

            // VS
            leftImageUrl = dto.left_image ?: "",
            rightImageUrl = dto.right_image ?: "",
            leftLabel = dto.left_label ?: "",
            rightLabel = dto.right_label ?: "",

            // Votes globaux
            leftVotesCount = dto.left_votes,
            rightVotesCount = dto.right_votes,
            totalVotesCount = totalVotes,

            // ✅ Vote utilisateur
            userVote = userVote
        )
    }
}