package com.nazam.instaclone.feature.profile.domain.repository

import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.profile.domain.model.Profile
import com.nazam.instaclone.feature.profile.domain.model.UpdateProfile

interface ProfileRepository {

    // Profil
    suspend fun getMyProfile(userId: String, emailFallback: String): Result<Profile>

    // Mes posts
    suspend fun getMyPosts(email: String): Result<List<VsPost>>

    // Update profil
    suspend fun updateMyProfile(userId: String, update: UpdateProfile): Result<Unit>


    suspend fun updateAvatar(userId: String, avatarUrl: String): Result<Unit>
}
