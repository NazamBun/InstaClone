package com.nazam.instaclone.core.di

import com.nazam.instaclone.core.supabase.SupabaseClientProvider
import com.nazam.instaclone.feature.auth.data.repository.AuthRepositoryImpl
import com.nazam.instaclone.feature.auth.domain.repository.AuthRepository
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LoginUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LogoutUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.SignupUseCase
import com.nazam.instaclone.feature.home.data.repository.HomeRepositoryImpl
import com.nazam.instaclone.feature.home.domain.repository.HomeRepository
import com.nazam.instaclone.feature.home.domain.usecase.CreatePostUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetFeedUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteLeftUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteRightUseCase
import org.koin.dsl.module

val appModule = module {

    // ----------------------------------------------------
    // ⭐ 1) SUPABASE CLIENT (singleton global)
    // ----------------------------------------------------
    single { SupabaseClientProvider.client }


    // ----------------------------------------------------
    // ⭐ 2) AUTHENTICATION
    // ----------------------------------------------------
    single<AuthRepository> { AuthRepositoryImpl(get()) }

    factory { LoginUseCase(get()) }
    factory { SignupUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }


    // ----------------------------------------------------
    // ⭐ 3) HOME / FEED VS (posts + votes + création)
    // ----------------------------------------------------
    single<HomeRepository> { HomeRepositoryImpl(get()) }

    // Use cases existants
    factory { GetFeedUseCase(get()) }
    factory { VoteLeftUseCase(get()) }
    factory { VoteRightUseCase(get()) }
    factory { CreatePostUseCase(get()) }

    // Nouveau use case pour créer un post VS
    factory { CreatePostUseCase(get()) }
}