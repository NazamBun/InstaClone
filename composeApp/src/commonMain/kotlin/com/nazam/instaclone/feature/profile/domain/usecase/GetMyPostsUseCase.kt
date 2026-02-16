package com.nazam.instaclone.feature.profile.domain.usecase

import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.profile.domain.repository.ProfileRepository

class GetMyPostsUseCase(
    private val repository: ProfileRepository
) {
    suspend fun execute(email: String): Result<List<VsPost>> {
        return repository.getMyPosts(email)
    }
}