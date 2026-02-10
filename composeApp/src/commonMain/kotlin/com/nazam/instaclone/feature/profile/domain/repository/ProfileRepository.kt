package com.nazam.instaclone.feature.profile.domain.repository

import com.nazam.instaclone.feature.profile.domain.model.Profile

interface ProfileRepository {
    suspend fun getMyProfile(userId: String, emailFallback: String): Result<Profile>
}
