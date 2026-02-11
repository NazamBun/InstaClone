package com.nazam.instaclone.feature.profile.domain.repository

import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.profile.domain.model.Profile

interface ProfileRepository {

    // ✅ Profil (table profiles)
    suspend fun getMyProfile(userId: String, emailFallback: String): Result<Profile>

    // ✅ Mes VS posts (view posts_feed) filtrés par email
    suspend fun getMyPosts(email: String): Result<List<VsPost>>
}