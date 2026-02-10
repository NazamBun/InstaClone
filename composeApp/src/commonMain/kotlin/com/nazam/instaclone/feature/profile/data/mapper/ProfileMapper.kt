package com.nazam.instaclone.feature.profile.data.mapper

import com.nazam.instaclone.feature.profile.data.dto.ProfileDto
import com.nazam.instaclone.feature.profile.domain.model.Profile

/**
 * Mapper (Clean)
 * DTO -> Domain
 */
object ProfileMapper {

    fun toDomain(
        dto: ProfileDto?,
        userId: String,
        email: String
    ): Profile {
        val displayName = dto?.displayName?.takeIf { it.isNotBlank() }
            ?: email.substringBefore("@").ifBlank { "User" }

        val username = dto?.username?.takeIf { it.isNotBlank() }
            ?: displayName.lowercase().replace(" ", "")

        return Profile(
            userId = userId,
            email = email,
            displayName = displayName,
            username = username,
            bio = dto?.bio.orEmpty(),
            location = dto?.location.orEmpty(),
            website = dto?.website.orEmpty(),
            avatarUrl = dto?.avatarUrl,
            coverUrl = dto?.coverUrl,
            joinedLabel = "—"
        )
    }
}
