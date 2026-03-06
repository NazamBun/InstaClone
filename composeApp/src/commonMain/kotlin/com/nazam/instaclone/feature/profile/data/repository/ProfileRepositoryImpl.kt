package com.nazam.instaclone.feature.profile.data.repository

import com.nazam.instaclone.feature.home.data.dto.PostDto
import com.nazam.instaclone.feature.home.data.mapper.PostMapper
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.profile.data.dto.ProfileDto
import com.nazam.instaclone.feature.profile.data.dto.UpdateProfileDto
import com.nazam.instaclone.feature.profile.data.mapper.ProfileMapper
import com.nazam.instaclone.feature.profile.domain.model.Profile
import com.nazam.instaclone.feature.profile.domain.model.UpdateProfile
import com.nazam.instaclone.feature.profile.domain.repository.ProfileRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class ProfileRepositoryImpl(
    private val client: SupabaseClient,
    private val json: Json
) : ProfileRepository {

    companion object {
        private const val PROFILES_TABLE = "profiles"
        private const val POSTS_FEED_VIEW = "posts_feed"
    }

    override suspend fun updateAvatar(
        userId: String,
        avatarUrl: String
    ): Result<Unit> {
        return runCatching {
            client.postgrest[PROFILES_TABLE].update(
                mapOf("avatar_url" to avatarUrl)
            ) {
                filter { eq("id", userId) }
            }

            Unit
        }
    }

    override suspend fun updateMyProfile(
        userId: String,
        update: UpdateProfile
    ): Result<Unit> {
        return runCatching {
            val payload = UpdateProfileDto(
                displayName = update.displayName.trim(),
                username = update.username.trim(),
                bio = update.bio.trim(),
                location = update.location.trim(),
                website = update.website.trim()
            )

            client.postgrest[PROFILES_TABLE].update(payload) {
                filter { eq("id", userId) }
            }

            Unit
        }
    }
}
