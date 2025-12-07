package com.nazam.instaclone.feature.home.data.mapper

import com.nazam.instaclone.feature.home.data.dto.PostDto
import com.nazam.instaclone.feature.home.domain.model.VsPost

object PostMapper {

    fun toDomain(dto: PostDto): VsPost {
        val totalVotes = dto.left_votes + dto.right_votes

        return VsPost(
            id = dto.id,
            authorName = dto.author_name ?: "Inconnu",
            authorAvatarUrl = dto.author_avatar,
            category = dto.category ?: "",
            // On simplifie pour l'instant : on ne parse pas la date
            createdAt = 0L,
            question = dto.question ?: "",
            leftImageUrl = dto.left_image ?: "",
            rightImageUrl = dto.right_image ?: "",
            leftLabel = dto.left_label ?: "",
            rightLabel = dto.right_label ?: "",
            leftVotesCount = dto.left_votes,
            rightVotesCount = dto.right_votes,
            totalVotesCount = totalVotes,
            isVotedLeft = false,   // on gèrera plus tard avec voted_left_ids
            isVotedRight = false
        )
    }
}