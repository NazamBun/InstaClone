package com.nazam.instaclone.feature.home.domain.usecase

import com.nazam.instaclone.feature.home.domain.repository.PostMediaRepository

class UploadPostImageUseCase(
    private val repository: PostMediaRepository
) {
    suspend fun execute(localUri: String): Result<String> {
        return repository.uploadPostImage(localUri)
    }
}