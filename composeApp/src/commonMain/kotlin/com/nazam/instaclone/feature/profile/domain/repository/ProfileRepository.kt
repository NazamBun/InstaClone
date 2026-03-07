package com.nazam.instaclone.feature.profile.domain.repository

import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.profile.domain.model.Profile
import com.nazam.instaclone.feature.profile.domain.model.UpdateProfile

interface ProfileRepository {
    suspend fun getMyProfile(userId: String, emailFallback: String): Result<Profile>
    suspend fun getMyPosts(email: String): Result<List<VsPost>>
    suspend fun updateMyProfile(userId: String, update: UpdateProfile): Result<Unit>
    suspend fun updateAvatar(userId: String, avatarUrl: String): Result<Unit>
    suspend fun getFollowersCount(userId: String): Result<Int>
    suspend fun getFollowingCount(userId: String): Result<Int>
}
