package com.nazam.instaclone.feature.home.data.mapper

import com.nazam.instaclone.feature.home.data.dto.PostDto
import com.nazam.instaclone.feature.home.domain.model.VoteChoice
import com.nazam.instaclone.feature.home.domain.model.VsPost
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

object PostMapper {

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
            createdAt = parseDate(dto.created_at),
            question = dto.question ?: "",
            leftImageUrl = dto.left_image ?: "",
            rightImageUrl = dto.right_image ?: "",
            leftLabel = dto.left_label ?: "",
            rightLabel = dto.right_label ?: "",
            leftVotesCount = dto.left_votes,
            rightVotesCount = dto.right_votes,
            totalVotesCount = totalVotes,
            selectedVsBadgeId = dto.vsBadgeId.orEmpty(),
            userVote = userVote
        )
    }

    @OptIn(ExperimentalTime::class)
    private fun parseDate(date: String?): Long {
        return try {
            date?.let { Instant.parse(it).toEpochMilliseconds() } ?: 0L
        } catch (_: Exception) {
            0L
        }
    }
}
