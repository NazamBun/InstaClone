package com.nazam.instaclone.feature.home.domain.usecase

import com.nazam.instaclone.feature.home.domain.model.UploadProgress
import com.nazam.instaclone.feature.home.domain.repository.PostMediaRepository
import kotlinx.coroutines.flow.Flow

class UploadPostImageUseCase(
    private val repository: PostMediaRepository
) {
    fun execute(localUri: String): Flow<UploadProgress> {
        return repository.uploadPostImage(localUri)
    }
}
