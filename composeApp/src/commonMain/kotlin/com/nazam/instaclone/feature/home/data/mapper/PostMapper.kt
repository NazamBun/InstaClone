package com.nazam.instaclone.feature.home.data.mapper

import com.nazam.instaclone.feature.home.data.dto.PostDto
import com.nazam.instaclone.feature.home.domain.model.VsPost

object PostMapper {

    fun toDomain(dto: PostDto): VsPost {
        val leftVotes = dto.left_votes
        val rightVotes = dto.right_votes

        return VsPost(
            id = dto.id,
            authorName = dto.author_name ?: "Inconnu",
            authorAvatarUrl = dto.author_avatar,
            category = dto.category ?: "",
            // pour l’instant on garde createdAt en Long simple = 0 (on fera mieux plus tard)
            createdAt = 0L,
            question = dto.question ?: "",
            leftImageUrl = dto.left_image ?: "",
            rightImageUrl = dto.right_image ?: "",
            leftLabel = dto.left_label ?: "",
            rightLabel = dto.right_label ?: "",
            leftVotesCount = leftVotes,
            rightVotesCount = rightVotes,
            totalVotesCount = leftVotes + rightVotes,
            // plus tard: on regardera les listes votedLeftList / votedRightList avec l’id utilisateur
            isVotedLeft = false,
            isVotedRight = false
        )
    }
}