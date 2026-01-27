package com.nazam.instaclone.feature.home.domain.usecase

/**
 * Upload gauche + droite, renvoie Pair(leftUrl, rightUrl)
 */
class UploadPostImagesUseCase(
    private val uploadOne: UploadPostImageUseCase
) {
    suspend fun execute(leftLocalUri: String, rightLocalUri: String): Result<Pair<String, String>> {
        val left = uploadOne.execute(leftLocalUri).getOrElse { return Result.failure(it) }
        val right = uploadOne.execute(rightLocalUri).getOrElse { return Result.failure(it) }
        return Result.success(left to right)
    }
}