package com.nazam.instaclone.core.di

import com.nazam.instaclone.core.supabase.SupabaseClientProvider
import com.nazam.instaclone.feature.auth.data.repository.AuthRepositoryImpl
import com.nazam.instaclone.feature.auth.domain.repository.AuthRepository
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LoginUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LogoutUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.SignupUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetFeedUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteLeftUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteRightUseCase
import org.koin.dsl.module
import com.nazam.instaclone.feature.home.domain.repository.HomeRepository
import com.nazam.instaclone.feature.home.data.repository.HomeRepositoryImpl


val appModule = module {
    // ⭐ Repository Home (Feed VS)
    single<HomeRepository> { HomeRepositoryImpl(get()) }

    // ⭐ Supabase client disponible partout
    single { SupabaseClientProvider.client }

    // ⭐ Repository Auth
    single<AuthRepository> { AuthRepositoryImpl(get()) }

    // ⭐ Use cases (Clean Architecture)
    factory { LoginUseCase(get()) }
    factory { SignupUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { GetFeedUseCase(get()) }
    factory { VoteLeftUseCase(get()) }
    factory { VoteRightUseCase(get()) }
}