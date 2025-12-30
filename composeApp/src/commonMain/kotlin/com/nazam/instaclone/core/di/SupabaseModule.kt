package com.nazam.instaclone.core.di

import com.nazam.instaclone.core.dispatchers.AppDispatchers
import com.nazam.instaclone.core.dispatchers.DefaultAppDispatchers
import com.nazam.instaclone.core.supabase.SupabaseClientProvider
import com.nazam.instaclone.feature.auth.data.repository.AuthRepositoryImpl
import com.nazam.instaclone.feature.auth.domain.repository.AuthRepository
import com.nazam.instaclone.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LoginUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.LogoutUseCase
import com.nazam.instaclone.feature.auth.domain.usecase.SignupUseCase
import com.nazam.instaclone.feature.auth.presentation.viewmodel.LoginViewModel
import com.nazam.instaclone.feature.auth.presentation.viewmodel.SignupViewModel
import com.nazam.instaclone.feature.home.data.repository.HomeRepositoryImpl
import com.nazam.instaclone.feature.home.domain.repository.HomeRepository
import com.nazam.instaclone.feature.home.domain.usecase.AddCommentUseCase
import com.nazam.instaclone.feature.home.domain.usecase.CreatePostUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetCommentsUseCase
import com.nazam.instaclone.feature.home.domain.usecase.GetFeedUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteLeftUseCase
import com.nazam.instaclone.feature.home.domain.usecase.VoteRightUseCase
import com.nazam.instaclone.feature.home.presentation.viewmodel.CreatePostViewModel
import com.nazam.instaclone.feature.home.presentation.viewmodel.HomeViewModel
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val appModule = module {

    // ✅ Supabase client
    single { SupabaseClientProvider.client }

    // ✅ Json
    single {
        Json { ignoreUnknownKeys = true }
    }

    // ✅ Dispatchers (KMP)
    single<AppDispatchers> { DefaultAppDispatchers() }

    // ✅ Auth
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    factory { LoginUseCase(get()) }
    factory { SignupUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }

    // ✅ Home repository
    single<HomeRepository> { HomeRepositoryImpl(client = get(), json = get()) }

    // ✅ Home use cases
    factory { GetFeedUseCase(get()) }
    factory { VoteLeftUseCase(get()) }
    factory { VoteRightUseCase(get()) }
    factory { CreatePostUseCase(get()) }
    factory { GetCommentsUseCase(get()) }
    factory { AddCommentUseCase(get()) }

    // ✅ ViewModels (via constructeur)
    // ✅ ViewModels (via constructeur)
    factory {
        HomeViewModel(
            dispatchers = get(),
            getFeedUseCase = get(),
            voteLeftUseCase = get(),
            voteRightUseCase = get(),
            getCommentsUseCase = get(),
            addCommentUseCase = get(),
            getCurrentUserUseCase = get(),
            logoutUseCase = get()
        )
    }

    factory {
        CreatePostViewModel(
            dispatchers = get(),
            createPostUseCase = get()
        )
    }

    factory {
        LoginViewModel(
            dispatchers = get(),
            loginUseCase = get(),
            getCurrentUserUseCase = get()
        )
    }

    factory {
        SignupViewModel(
            dispatchers = get(),
            signupUseCase = get()
        )
    }
}