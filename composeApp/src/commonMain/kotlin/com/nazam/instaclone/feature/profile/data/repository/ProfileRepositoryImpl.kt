package com.nazam.instaclone.feature.profile.data.repository

import com.nazam.instaclone.feature.home.data.dto.PostDto
import com.nazam.instaclone.feature.home.data.mapper.PostMapper
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.profile.data.dto.ProfileDto
import com.nazam.instaclone.feature.profile.data.mapper.ProfileMapper
import com.nazam.instaclone.feature.profile.domain.model.Profile
import com.nazam.instaclone.feature.profile.domain.repository.ProfileRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * ProfileRepositoryImpl
 * - profil : table "profiles"
 * - mes posts : view "posts_feed" filtrée par author_name (= email)
 */
class ProfileRepositoryImpl(
    private val client: SupabaseClient,
    private val json: Json
) : ProfileRepository {

    companion object {
        private const val PROFILES_TABLE = "profiles"
        private const val POSTS_FEED_VIEW = "posts_feed"
    }

    override suspend fun getMyProfile(userId: String, emailFallback: String): Result<Profile> {
        return runCatching {
            // 1 ligne max
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
                    // ✅ Comme tes posts sont créés avec author_name = email
                    filter { eq("author_name", email) }
                }

            val dtos: List<PostDto> = json.decodeFromString(
                ListSerializer(PostDto.serializer()),
                response.data
            )

            dtos.map { dto ->
                // user_choice est déjà géré par HomeRepository (mais ici on veut juste l’affichage)
                // donc on laisse VoteChoice.NONE pour l’instant.
                PostMapper.toDomain(dto)
            }
        }
    }
}