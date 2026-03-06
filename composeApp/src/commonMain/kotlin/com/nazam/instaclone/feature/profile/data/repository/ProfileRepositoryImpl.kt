package com.nazam.instaclone.feature.profile.data.repository

import com.nazam.instaclone.feature.home.data.dto.PostDto
import com.nazam.instaclone.feature.home.data.mapper.PostMapper
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.profile.data.dto.ProfileDto
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
            val dtos = fetchProfiles(userId)
            val dto = dtos.firstOrNull()

            ProfileMapper.toDomain(
                dto = dto,
                userId = userId,
                email = emailFallback
            )
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
            val payload = mapOf(
                "display_name" to update.displayName.trim(),
                "username" to update.username.trim(),
                "bio" to update.bio.trim(),
                "location" to update.location.trim(),
                "website" to update.website.trim()
            )

            if (hasProfile(userId)) {
                client.postgrest[PROFILES_TABLE].update(payload) {
                    filter { eq("id", userId) }
                }
            } else {
                client.postgrest[PROFILES_TABLE].insert(
                    payload + ("id" to userId)
                )
            }

            Unit
        }
    }

    override suspend fun updateAvatar(
        userId: String,
        avatarUrl: String
    ): Result<Unit> {
        return runCatching {
            val payload = mapOf("avatar_url" to avatarUrl)

            if (hasProfile(userId)) {
                client.postgrest[PROFILES_TABLE].update(payload) {
                    filter { eq("id", userId) }
                }
            } else {
                client.postgrest[PROFILES_TABLE].insert(
                    payload + ("id" to userId)
                )
            }

            Unit
        }
    }

    private suspend fun hasProfile(userId: String): Boolean {
        return fetchProfiles(userId).isNotEmpty()
    }

    private suspend fun fetchProfiles(userId: String): List<ProfileDto> {
        val response = client
            .postgrest[PROFILES_TABLE]
            .select {
                filter { eq("id", userId) }
                limit(1)
            }

        return json.decodeFromString(
            ListSerializer(ProfileDto.serializer()),
            response.data
        )
    }
}
