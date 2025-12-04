package com.nazam.instaclone.feature.home.data.repository

import com.nazam.instaclone.core.supabase.SupabaseClientProvider
import com.nazam.instaclone.feature.home.data.dto.PostDto
import com.nazam.instaclone.feature.home.data.mapper.PostMapper
import com.nazam.instaclone.feature.home.domain.model.VsPost
import com.nazam.instaclone.feature.home.domain.repository.HomeRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class HomeRepositoryImpl(
    private val client: SupabaseClient = SupabaseClientProvider.client
) : HomeRepository {

    override suspend fun getFeed(): Result<List<VsPost>> {
        return runCatching {
            val dtos = client
                .from("posts")
                .select()             // SELECT * FROM posts
                .decodeList<PostDto>() // on récupère une List<PostDto>

            dtos.map { PostMapper.toDomain(it) }
        }
    }

    override suspend fun voteLeft(postId: String): Result<VsPost> {
        // On implémentera la vraie logique de vote plus tard.
        // Pour le moment, on met un TODO() pour que ça compile.
        return Result.failure(NotImplementedError("voteLeft not implemented yet"))
    }

    override suspend fun voteRight(postId: String): Result<VsPost> {
        // Même chose ici, on verra plus tard.
        return Result.failure(NotImplementedError("voteRight not implemented yet"))
    }
}