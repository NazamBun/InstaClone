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

    override suspend fun getMyProfile(
        userId: String,
        emailFallback: String
    ): Result<Profile> {
        return runCatching {
            val response = client
                .postgrest[PROFILES_TABLE]
                .select {
                    filter { eq("id", userId) }
                    limit(1)
                }

            val dtos: List<ProfileDto> = json.decodeFromString(
                ListSerializer(ProfileDto.serializer()),
                response.data
            )

            val dto = dtos.firstOrNull()
            ProfileMapper.toDomain(dto = dto, userId = userId, email = emailFallback)
        }
    }

    override suspend fun getMyPosts(email: String): Result<List<VsPost>> {
        return runCatching {
            val response = client
                .postgrest[POSTS_FEED_VIEW]
                .select {
                    filter { eq("author_name", email) }
                }

            val dtos: List<PostDto> = json.decodeFromString(
                ListSerializer(PostDto.serializer()),
                response.data
            )

            dtos.map { dto -> PostMapper.toDomain(dto) }
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
