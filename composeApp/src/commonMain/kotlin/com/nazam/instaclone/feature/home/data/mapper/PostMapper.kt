package com.nazam.instaclone.feature.home.data.mapper

import com.nazam.instaclone.feature.home.data.dto.PostDto
import com.nazam.instaclone.feature.home.domain.model.VoteChoice
import com.nazam.instaclone.feature.home.domain.model.VsPost

object PostMapper {

    /**
     * Mappe un PostDto (Supabase) vers VsPost (domaine).
     */
    fun toDomain(
        dto: PostDto,
        userVote: VoteChoice = VoteChoice.NONE
    ): VsPost {
        val totalVotes = dto.left_votes + dto.right_votes

        return VsPost(
            id = dto.id,
            authorId = dto.authorId,
            authorName = dto.author_name ?: "Inconnu",
            authorAvatarUrl = dto.author_avatar,
            category = dto.category ?: "",
            createdAt = 0L,
            question = dto.question ?: "",
            leftImageUrl = dto.left_image ?: "",
            rightImageUrl = dto.right_image ?: "",
            leftLabel = dto.left_label ?: "",
            rightLabel = dto.right_label ?: "",
            leftVotesCount = dto.left_votes,
            rightVotesCount = dto.right_votes,
            totalVotesCount = totalVotes,
            userVote = userVote
        )
    }
}
