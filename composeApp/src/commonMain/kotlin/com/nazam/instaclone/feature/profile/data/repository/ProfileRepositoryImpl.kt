package com.nazam.instaclone.feature.profile.data.repository

import com.nazam.instaclone.feature.profile.data.dto.ProfileDto
import com.nazam.instaclone.feature.profile.data.mapper.ProfileMapper
import com.nazam.instaclone.feature.profile.domain.model.Profile
import com.nazam.instaclone.feature.profile.domain.repository.ProfileRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

/**
 * Data layer Supabase.
 * On lit la table "profiles" par id = userId.
 */
class ProfileRepositoryImpl(
    private val client: SupabaseClient
) : ProfileRepository {

    override suspend fun getMyProfile(userId: String, emailFallback: String): Result<Profile> {
        return runCatching {
            val dto = client.postgrest["profiles"]
                .select {
                    filter { eq("id", userId) }
                    limit(1)
                }
                .decodeList<ProfileDto>()
                .firstOrNull()

            ProfileMapper.toDomain(
                dto = dto,
                userId = userId,
                email = emailFallback
            )
        }
    }
}
