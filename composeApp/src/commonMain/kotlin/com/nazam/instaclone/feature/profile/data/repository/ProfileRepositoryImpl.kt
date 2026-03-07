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

    private companion object {
        const val PROFILES_TABLE = "profiles"
        const val POSTS_FEED_VIEW = "posts_feed"
        const val FOLLOWS_TABLE = "follows"
    }

    override suspend fun getMyProfile(
        userId: String,
        emailFallback: String
    ): Result<Profile> {
        return runCatching {
            val dto = fetchProfiles(userId).firstOrNull()
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

            dtos.map(PostMapper::toDomain)
        }
    }

    override suspend fun updateMyProfile(
        userId: String,
        update: UpdateProfile
    ): Result<Unit> {
        return runCatching {
            val payload = mutableMapOf<String, Any>(
                "display_name" to update.displayName.trim()
            )

            val username = update.username.trim()
            val bio = update.bio.trim()
            val location = update.location.trim()
            val website = update.website.trim()

            if (username.isNotBlank()) payload["username"] = username
            if (bio.isNotBlank()) payload["bio"] = bio
            if (location.isNotBlank()) payload["location"] = location
            if (website.isNotBlank()) payload["website"] = website

            if (hasProfile(userId)) {
                client.postgrest[PROFILES_TABLE].update(payload) {
                    filter { eq("id", userId) }
                }
            } else {
                client.postgrest[PROFILES_TABLE].insert(payload + ("id" to userId))
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
                client.postgrest[PROFILES_TABLE].insert(payload + ("id" to userId))
            }

            Unit
        }
    }

    override suspend fun getFollowersCount(userId: String): Result<Int> {
        return runCatching {
            val response = client
                .postgrest[FOLLOWS_TABLE]
                .select {
                    filter { eq("following_id", userId) }
                }

            response.data.countOccurrences("follower_id")
        }
    }

    override suspend fun getFollowingCount(userId: String): Result<Int> {
        return runCatching {
            val response = client
                .postgrest[FOLLOWS_TABLE]
                .select {
                    filter { eq("follower_id", userId) }
                }

            response.data.countOccurrences("following_id")
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

    private fun String.countOccurrences(key: String): Int {
        return "\"$key\"".toRegex().findAll(this).count()
    }
}
